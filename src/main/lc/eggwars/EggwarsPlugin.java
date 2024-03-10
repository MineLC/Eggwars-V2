package lc.eggwars;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.tinylog.Logger;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.commands.BasicCommandsRegister;
import lc.eggwars.commands.InfoCommand;
import lc.eggwars.commands.game.GameCommand;
import lc.eggwars.commands.map.MapCreatorCommand;
import lc.eggwars.database.MongoDBHandler;
import lc.eggwars.game.StartGameData;
import lc.eggwars.game.generators.GeneratorThread;
import lc.eggwars.game.generators.StartGenerators;
import lc.eggwars.game.pregameitems.StartPreGameItems;
import lc.eggwars.game.shop.Shop;
import lc.eggwars.game.shop.StartShops;
import lc.eggwars.game.shop.shopkeepers.StartShopkeepers;
import lc.eggwars.listeners.PlayerBreakListener;
import lc.eggwars.listeners.PlayerInteractListener;
import lc.eggwars.listeners.PlayerJoinListener;
import lc.eggwars.listeners.PlayerQuitListener;
import lc.eggwars.listeners.gameshop.GameShopInventoryClickListener;
import lc.eggwars.listeners.gameshop.ShopkeeperListener;
import lc.eggwars.listeners.inventory.PlayerInventoryClickListener;
import lc.eggwars.listeners.map.CompleteWorldGenerateListener;
import lc.eggwars.listeners.pvp.EntityDamageListener;
import lc.eggwars.listeners.pvp.PlayerDamageByPlayerListener;
import lc.eggwars.listeners.pvp.PlayerDeathListener;
import lc.eggwars.listeners.pvp.PlayerRespawnListener;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
import lc.eggwars.messages.StartMessages;
import lc.eggwars.others.deaths.StartDeaths;
import lc.eggwars.others.kits.StartKits;
import lc.eggwars.others.levels.StartLevels;
import lc.eggwars.others.sidebar.StartSidebar;
import lc.eggwars.others.spawn.SpawnStorage;
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
                Logger.error(e);
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
        new StartPreGameItems().load(this);
        new StartSidebar().load(this);

        final IntObjectHashMap<Shop> shops = new StartShops().load(this);
        new StartShopkeepers().load(this, shops);

        final ListenerRegister listeners = new ListenerRegister(this);

        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
            final CompletableFuture<?> loadMapTask = new StartMaps(this).load(slimePlugin);
            if (loadMapTask != null) {
                loadMapTask.thenAcceptAsync((value) -> {
                    new StartSpawn(this).loadSpawn();
                    Logger.info("Maps and spawn are now loaded");
                });
            };
        }, 40);

        registerBasicListeners(listeners);
        listeners.register(new GameShopInventoryClickListener(shops), false);
    }

    @Override
    public void onDisable() {
        DATABASE.shutdown();

        final List<World> worlds = Bukkit.getWorlds();

        for (final World world : worlds) {
            Bukkit.unloadWorld(world, false);
        }

        GeneratorThread.setThread(null);
        getServer().getScheduler().cancelTasks(this);
    }

    private void registerBasicListeners(final ListenerRegister register) {
        register.register(new PlayerDeathListener(), true);
        register.register(new PlayerRespawnListener(), true);
        register.register(new EntityDamageListener(), true);
        register.register(new PlayerDamageByPlayerListener(), true);
        register.register(new PlayerInventoryClickListener(this), true);
        register.register(new PlayerInteractListener(), true);
        register.register(new CompleteWorldGenerateListener(), true);

        register.register(new ShopkeeperListener(), false);
        register.register(new PlayerBreakListener(), true);
        register.register(new PlayerJoinListener(), true);  
        register.register(new PlayerQuitListener(), true);  

        register.cancelEvent(BlockPhysicsEvent.class);
        register.cancelEvent(BlockGrowEvent.class);
        register.fastListener(PlayerDropItemEvent.class, (event) -> {
            ((PlayerDropItemEvent)event).getPlayer().sendMessage("IS IN SPAWN: " + (SpawnStorage.getStorage().isInSpawn(((PlayerDropItemEvent)event).getPlayer())));
            if (SpawnStorage.getStorage().isInSpawn(((PlayerDropItemEvent)event).getPlayer())) {
                ((PlayerDropItemEvent)event).setCancelled(true);
            }
        });
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
        CommandStorage.register(new InfoCommand(), "info");
        new BasicCommandsRegister().registerBasicCommands();
    }
}