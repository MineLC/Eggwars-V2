package lc.eggwars;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import lc.eggwars.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.tinylog.Logger;

import lc.eggwars.commands.BasicCommandsRegister;
import lc.eggwars.commands.InfoCommand;
import lc.eggwars.commands.game.GameCommand;
import lc.eggwars.commands.game.LeaveCommand;
import lc.eggwars.commands.map.MapCreatorCommand;
import lc.eggwars.database.MongoDBHandler;
import lc.eggwars.game.GameManagerThread;
import lc.eggwars.game.StartGameData;
import lc.eggwars.game.generators.StartGenerators;
import lc.eggwars.game.pregame.StartPreGameData;
import lc.eggwars.game.shop.ShopsData;
import lc.eggwars.game.shop.StartShops;
import lc.eggwars.game.shop.shopkeepers.StartShopkeepers;
import lc.eggwars.listeners.gameshop.ShopkeeperListener;
import lc.eggwars.listeners.inventory.PlayerInventoryClickListener;
import lc.eggwars.listeners.map.CompleteWorldGenerateListener;
import lc.eggwars.listeners.pvp.PlayerDeathListener;
import lc.eggwars.listeners.pvp.PlayerRespawnListener;
import lc.eggwars.listeners.pvp.damage.EntityDamageListener;
import lc.eggwars.listeners.pvp.damage.PlayerDamageByPlayerListener;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
import lc.eggwars.messages.StartMessages;
import lc.eggwars.others.deaths.StartDeaths;
import lc.eggwars.others.kits.StartKits;
import lc.eggwars.others.levels.StartLevels;
import lc.eggwars.others.sidebar.StartSidebar;
import lc.eggwars.others.spawn.StartSpawn;
import lc.eggwars.teams.StartTeams;

import lc.lcspigot.commands.CommandStorage;
import lc.lcspigot.listeners.ListenerRegister;

import net.swofty.swm.api.SlimePlugin;

public class EggwarsPlugin extends JavaPlugin {

    private static final MongoDBHandler DATABASE = new MongoDBHandler();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        final SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SwoftyWorldManager");
        if (slimePlugin == null) {
            Logger.error("EggwarsCore need slimeworld manager to work");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {   
                DATABASE.init(this);   
            } catch (Exception e) {
                getServer().getScheduler().runTask(this, () -> Logger.error(e));
            }
        });

        loadCommands(slimePlugin);

        new StartMessages().load(this);
        new StartGenerators().load(this);
        new StartTeams(this).load();
        new StartGameData().load(this);
        new StartKits(this).load();
        new StartDeaths(this).load(this);
        new StartSpawn(this).loadItems();
        new StartLevels(this).load();
        new StartPreGameData().loadItems(this);
        new StartSidebar().load(this);

        final ShopsData data = new StartShops().load(this);
        new StartShopkeepers().load(this, data.shops());

        final CompletableFuture<Void> loadingMaps = new StartMaps(this, slimePlugin).load();
        if (loadingMaps != null) {
            loadingMaps.thenAccept((none) -> {
                new StartSpawn(this).loadSpawn();
            });
            GameManagerThread.startThread();
        }

        registerBasicListeners(data);
    }

    @Override
    public void onDisable() {
        DATABASE.shutdown();

        final SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SwoftyWorldManager");
        if (slimePlugin != null) {
            slimePlugin.getSlimeWorlds().values().forEach((world) -> world.unloadWorld(false));
            return;
        }

        GameManagerThread.stopThread();
        getServer().getScheduler().cancelTasks(this);
    }

    private void registerBasicListeners(final ShopsData shopsData) {
        final ListenerRegister listeners = new ListenerRegister(this);

        listeners.register(new PlayerDeathListener(this), true);
        listeners.register(new PlayerRespawnListener(this), true);
        listeners.register(new EntityDamageListener(), true);
        listeners.register(new PlayerDamageByPlayerListener(), true);
        listeners.register(new PlayerInventoryClickListener(this, shopsData), true);
        listeners.register(new PlayerInteractListener(), true);

        listeners.register(new ShopkeeperListener(), false);
        listeners.register(new PlayerBreakListener(), true);
        listeners.register(new PlayerJoinListener(), true);  
        listeners.register(new PlayerQuitListener(), true);  
        listeners.register(new PlayerDropitemListener(), true);  
        listeners.register(new CompleteWorldGenerateListener(), true);
        listeners.register(new PlayerChatListener(), true);

        listeners.cancelEvent(BlockPhysicsEvent.class);
        listeners.cancelEvent(BlockGrowEvent.class);
    }

    public FileConfiguration loadConfig(final String name) {
        final String fileFormat = name + ".yml";
        final File file = new File(getDataFolder(), fileFormat);
        if (!file.exists()) {
            saveResource(fileFormat, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void tryCreateFiles(final String... files) {
        for (final String file : files) {
            if (!new File(file).exists()) {
                saveResource(file, false);
            }
        }
    }

    private void loadCommands(SlimePlugin slimePlugin) {
        CommandStorage.register(new MapCreatorCommand(slimePlugin, this, new MapCreatorData()), "map");
        CommandStorage.register(new GameCommand(this), "game");
        CommandStorage.register(new LeaveCommand(), "leave");
        CommandStorage.register(new InfoCommand(), "info");

        new BasicCommandsRegister().registerBasicCommands();
    }
}