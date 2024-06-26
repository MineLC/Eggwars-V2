package lc.eggwars;

import java.io.File;

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
import lc.eggwars.inventory.types.SelectMapInventory;
import lc.eggwars.listeners.gameshop.ShopkeeperListener;
import lc.eggwars.listeners.inventory.PlayerInventoryClickListener;
import lc.eggwars.listeners.pvp.PlayerDeathListener;
import lc.eggwars.listeners.pvp.PlayerRespawnListener;
import lc.eggwars.listeners.pvp.damage.EntityDamageListener;
import lc.eggwars.listeners.pvp.damage.PlayerDamageByPlayerListener;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
import lc.eggwars.messages.Messages;
import lc.eggwars.messages.StartMessages;
import lc.eggwars.others.deaths.StartDeaths;
import lc.eggwars.others.events.StartEvents;
import lc.eggwars.others.kits.StartKits;
import lc.eggwars.others.levels.StartLevels;
import lc.eggwars.others.selectgame.StartMapInventories;
import lc.eggwars.others.sidebar.StartSidebar;
import lc.eggwars.others.spawn.StartSpawn;
import lc.eggwars.others.tab.StartTab;
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

        try {
            MONGODB.init(this);
            getServer().getScheduler().runTaskLater(this, () -> {

                new StartSpawn(this).loadSpawn();
                new StartPreGameData().loadMap(this);
                new StartMaps(this, slimePlugin).load();
                GameManagerThread.startThread();   
    
            }, 40);

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
            new StartTab().load(this);
    
            final ShopsData data = new StartShops().load(this);
            final SelectMapInventory selectMapInventory = new StartMapInventories().load(this);
    
            new StartShopkeepers().load(this, data.shops());
            registerBasicListeners(data, selectMapInventory);
        } catch (Exception e) {
            Logger.error(e);
        }

    }

    @Override
    public void onDisable() {
        MONGODB.shutdown();

        GameManagerThread.stopThread();
        getServer().getScheduler().cancelTasks(this);
    }

    private void registerBasicListeners(final ShopsData shopsData, final SelectMapInventory selectMapInventory) {
        final ListenerRegister listeners = new ListenerRegister(this);

        listeners.register(new ShopkeeperListener(), false);
        listeners.register(new PlayerJoinTabInfoListener(), false);

        listeners.register(new PlayerDeathListener(this), true);
        listeners.register(new PlayerRespawnListener(this), true);
        listeners.register(new EntityDamageListener(), true);
        listeners.register(new PlayerDamageByPlayerListener(), true);
        listeners.register(new PlayerInventoryClickListener(this, shopsData, selectMapInventory), true);
        listeners.register(new PlayerInteractListener(selectMapInventory), true);
        listeners.register(new PlayerJoinListener(Messages.color(getConfig().getString("join"))), true);  
        listeners.register(new PlayerQuitListener(), true);  
        listeners.register(new PlayerBreakListener(), true);  
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