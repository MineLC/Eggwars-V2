package lc.eggwars;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import lc.eggwars.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import com.grinderwolf.swm.plugin.SWMPlugin;

import lc.eggwars.commands.BasicCommandsRegister;
import lc.eggwars.commands.InfoCommand;
import lc.eggwars.commands.game.LeaveCommand;
import lc.eggwars.commands.map.MapCreatorCommand;
import lc.eggwars.database.mongodb.MongoDBHandler;
import lc.eggwars.game.GameManagerThread;
import lc.eggwars.game.StartGameData;
import lc.eggwars.game.generators.StartGenerators;
import lc.eggwars.game.pregame.StartPreGameData;
import lc.eggwars.game.shop.ShopsData;
import lc.eggwars.game.shop.StartShops;
import lc.eggwars.game.shop.shopkeepers.StartShopkeepers;
import lc.eggwars.listeners.gameshop.ShopkeeperListener;
import lc.eggwars.listeners.inventory.PlayerInventoryClickListener;
import lc.eggwars.listeners.pvp.PlayerDeathListener;
import lc.eggwars.listeners.pvp.PlayerRespawnListener;
import lc.eggwars.listeners.pvp.damage.EntityDamageListener;
import lc.eggwars.listeners.pvp.damage.PlayerDamageByPlayerListener;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
import lc.eggwars.messages.StartMessages;
import lc.eggwars.others.deaths.StartDeaths;
import lc.eggwars.others.events.StartEvents;
import lc.eggwars.others.kits.StartKits;
import lc.eggwars.others.levels.StartLevels;
import lc.eggwars.others.selectgame.MapInventoryBuilder;
import lc.eggwars.others.selectgame.StartMapInventories;
import lc.eggwars.others.sidebar.StartSidebar;
import lc.eggwars.others.spawn.StartSpawn;
import lc.eggwars.teams.StartTeams;

import lc.lcspigot.commands.CommandStorage;
import lc.lcspigot.listeners.ListenerRegister;


public class EggwarsPlugin extends JavaPlugin {

    private static final MongoDBHandler MONGODB = new MongoDBHandler();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        final SWMPlugin slimePlugin = (SWMPlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if (slimePlugin == null) {
            Logger.info("EggwarsCore need slimeworld manager to work");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {   
                MONGODB.init(this);

                new StartSpawn(this).loadSpawn();
                new StartPreGameData().loadMap(this);
                GameManagerThread.startThread();   
            } catch (Exception e) {
                getServer().getScheduler().runTask(this, () -> Logger.error(e));
                setEnabled(false);
            }
        });
        loadCommands();

        new StartMessages().load(this);
        new StartGenerators().load(this);
        new StartTeams(this).load();
        new StartGameData().load(this);
        new StartKits(this).load();
        new StartDeaths(this).load(this);
        new StartSpawn(this).loadItems();
        new StartLevels(this).load();
        new StartPreGameData().loadItems(this);
        new StartSidebar(this).load();
        new StartEvents(this).load();
        new StartMaps(this, slimePlugin).load();

        final ShopsData data = new StartShops().load(this);
        final MapInventoryBuilder mapInventoryBuilder = new StartMapInventories().load(this);

        new StartShopkeepers().load(this, data.shops());
        registerBasicListeners(data, mapInventoryBuilder);
    }

    @Override
    public void onDisable() {
        MONGODB.shutdown();

        GameManagerThread.stopThread();
        getServer().getScheduler().cancelTasks(this);
    }

    private void registerBasicListeners(final ShopsData shopsData, final MapInventoryBuilder mapInventoryBuilder) {
        final ListenerRegister listeners = new ListenerRegister(this);

        listeners.register(new PlayerDeathListener(this), true);
        listeners.register(new PlayerRespawnListener(this), true);
        listeners.register(new EntityDamageListener(), true);
        listeners.register(new PlayerDamageByPlayerListener(), true);
        listeners.register(new PlayerInventoryClickListener(this, shopsData), true);
        listeners.register(new PlayerInteractListener(mapInventoryBuilder), true);

        listeners.register(new ShopkeeperListener(), false);
        listeners.register(new PlayerBreakListener(), true);
        listeners.register(new PlayerJoinListener(getConfig().getStringList("tab.header"), getConfig().getStringList("tab.footer")), true);  
        listeners.register(new PlayerQuitListener(), true);  
        listeners.register(new PlayerDropitemListener(), true);  
        listeners.register(new PlayerChatListener(), true);
        listeners.register(new PlayerSaturationEvent(), true);
        listeners.register(new ItemPickupListener(), true);

        listeners.cancelEvent(BlockPhysicsEvent.class);
        listeners.cancelEvent(BlockGrowEvent.class);
        listeners.cancelEvent(PlayerArmorStandManipulateEvent.class);
        listeners.cancelEvent(WeatherChangeEvent.class);
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

    private void loadCommands() {
        CommandStorage.register(new MapCreatorCommand(this, new MapCreatorData()), "map");
        CommandStorage.register(new LeaveCommand(), "leave");
        CommandStorage.register(new InfoCommand(), "info");

        new BasicCommandsRegister().registerBasicCommands();
    }
}