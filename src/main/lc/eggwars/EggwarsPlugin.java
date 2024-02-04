package lc.eggwars;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.grinderwolf.swm.api.SlimePlugin;

import lc.eggwars.commands.map.MapCreatorCommand;
import lc.eggwars.generators.StartGenerators;
import lc.eggwars.listeners.PlayerInteractListener;
import lc.eggwars.listeners.internal.ListenerRegister;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
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
        new ListenerRegister(this).register(new PlayerInteractListener());
        final PluginCommand command = getCommand("map");
        if (command == null) {
            getLogger().log(Level.SEVERE, "Error on load map command. ¿Invalid plugin.yml?");
            return;
        }

        final MapCreatorCommand mapCommand = new MapCreatorCommand(this, new MapCreatorData());
        command.setExecutor(mapCommand);       
        command.setTabCompleter(mapCommand);    

        new StartGenerators(this).load();
        new StartTeams(this).load();
        
        // Remember start maps on final
        // This is executed 20 ticks later for wait to load all worlds
        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> new StartMaps(this).load(), 20);
    }

    @Override
    public void onDisable() {
        final List<World> worlds = Bukkit.getWorlds();

        for (final World world : worlds) {
            Bukkit.unloadWorld(world, false);
        }
    }

    public FileConfiguration loadConfig(final String name) {
        final String fileFormat = name + ".yml";
        final File file = new File(getDataFolder(), fileFormat);
        if (!file.exists()) {
            saveResource(fileFormat, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}