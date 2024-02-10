package lc.eggwars;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.grinderwolf.swm.api.SlimePlugin;

import lc.eggwars.commands.game.GameCommand;
import lc.eggwars.commands.map.MapCreatorCommand;
import lc.eggwars.game.StartGameData;
import lc.eggwars.generators.GeneratorThread;
import lc.eggwars.generators.StartGenerators;
import lc.eggwars.listeners.BlockPhysicsListener;
import lc.eggwars.listeners.EntityDamageListener;
import lc.eggwars.listeners.PlayerDeathListener;
import lc.eggwars.listeners.PlayerInteractListener;
import lc.eggwars.listeners.PlayerJoinListener;
import lc.eggwars.listeners.PlayerRespawnListener;
import lc.eggwars.listeners.internal.ListenerRegister;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
import lc.eggwars.messages.Messages;
import lc.eggwars.messages.StartMessages;
import lc.eggwars.spawn.StartSpawn;
import lc.eggwars.teams.StartTeams;

public class EggwarsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if (slimePlugin == null) {
            getLogger().log(Level.SEVERE, "EggwarsCore need slimeworld manager to work");
            return;
        }

        if (!loadCommands()) {
            getLogger().log(Level.SEVERE, "Error on load commands. ¿Invalid plugin.yml?");
            return;
        }
        final Messages messages = new StartMessages().load(this);

        new StartGenerators().load(this);
        new StartTeams(this).load();
        new StartGameData().load(this, messages);

        final ListenerRegister listeners = new ListenerRegister(this);
   
        // Remember start maps on final
        // This is executed 20 ticks later for wait to load all worlds
        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
            new StartMaps(this).load(slimePlugin);
            final Location spawnLocation = new StartSpawn(this).load();

            listeners.register(new PlayerInteractListener());
            if (spawnLocation != null) {
                listeners.register(new PlayerJoinListener());  
            }
        }, 20);

        listeners.register(new PlayerDeathListener());
        listeners.register(new PlayerRespawnListener(this, getConfig()));
        listeners.register(new BlockPhysicsListener());
        listeners.register(new EntityDamageListener());
    }

    @Override
    public void onDisable() {
        final List<World> worlds = Bukkit.getWorlds();

        for (final World world : worlds) {
            Bukkit.unloadWorld(world, false);
        }

        GeneratorThread.setThread(null);
        getServer().getScheduler().cancelTasks(this);
    }

    public FileConfiguration loadConfig(final String name) {
        final String fileFormat = name + ".yml";
        final File file = new File(getDataFolder(), fileFormat);
        if (!file.exists()) {
            saveResource(fileFormat, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    private boolean loadCommands() {
        final PluginCommand pluginMapCommand = getCommand("map");
        final PluginCommand pluginGameCommand = getCommand("game");

        if (pluginMapCommand == null || pluginGameCommand == null) {
            return false;
        }

        final MapCreatorCommand mapCommand = new MapCreatorCommand(this, new MapCreatorData());
        pluginMapCommand.setExecutor(mapCommand);       
        pluginMapCommand.setTabCompleter(mapCommand);    

        final GameCommand gameCommand = new GameCommand(this);
        pluginGameCommand.setExecutor(gameCommand);
        pluginGameCommand.setTabCompleter(gameCommand);
        return true;
    }
}