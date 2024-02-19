package lc.eggwars;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import lc.eggwars.commands.game.GameCommand;
import lc.eggwars.commands.map.MapCreatorCommand;
import lc.eggwars.game.StartGameData;
import lc.eggwars.game.generators.GeneratorThread;
import lc.eggwars.game.generators.StartGenerators;
import lc.eggwars.game.shopkeepers.StartShopkeepers;
import lc.eggwars.inventory.StartInventories;
import lc.eggwars.listeners.PlayerInteractListener;
import lc.eggwars.listeners.PlayerInventoryClickListener;
import lc.eggwars.listeners.PlayerJoinListener;
import lc.eggwars.listeners.map.CompleteWorldGenerateListener;
import lc.eggwars.listeners.pvp.EntityDamageListener;
import lc.eggwars.listeners.pvp.PlayerDamageByPlayerListener;
import lc.eggwars.listeners.pvp.PlayerDeathListener;
import lc.eggwars.listeners.pvp.PlayerRespawnListener;
import lc.eggwars.listeners.shopkeepers.PreInteractWithEntityListener;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.mapsystem.StartMaps;
import lc.eggwars.messages.StartMessages;
import lc.eggwars.others.deaths.StartDeaths;
import lc.eggwars.spawn.SpawnStorage;
import lc.eggwars.spawn.StartSpawn;
import lc.eggwars.teams.StartTeams;
import lc.lcspigot.commands.CommandStorage;
import lc.lcspigot.listeners.ListenerRegister;
import net.swofty.swm.api.SlimePlugin;

public class EggwarsPlugin extends JavaPlugin {

    private static EggwarsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        final SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SwoftyWorldManager");
        if (slimePlugin == null) {
            Logger.error("EggwarsCore need slimeworld manager to work");
            return;
        }

        loadCommands(slimePlugin);

        new StartMessages().load(this);
        new StartShopkeepers().load(this);
        new StartInventories().load();
        new StartGenerators().load(this);
        new StartTeams(this).load();
        new StartGameData().load(this);
        new StartSpawn(this).load();

        final ListenerRegister listeners = new ListenerRegister(this);
   
        // Remember start maps on final
        // This is executed 20 ticks later for wait to load all worlds
        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
            new StartMaps(this).load(slimePlugin);
        }, 20);

        registerListeners(listeners);
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


    private void registerListeners(final ListenerRegister register) {
        register.register(new PlayerDeathListener(), true);
        register.register(new PlayerRespawnListener(this, new StartDeaths(this)), true);
        register.register(new EntityDamageListener(), true);
        register.register(new PlayerDamageByPlayerListener(), true);
        register.register(new PlayerInventoryClickListener(), true);
        register.register(new PlayerInteractListener(), true);
        register.register(new CompleteWorldGenerateListener(), true);

        register.register(new PreInteractWithEntityListener(), false);

        if (SpawnStorage.getStorage().getLocation() != null) {
            register.register(new PlayerJoinListener(), true);  
        }

        register.cancelEvent(BlockPhysicsEvent.class);
        register.cancelEvent(BlockGrowEvent.class);
        register.fastListener(PlayerDropItemEvent.class, (event) -> {
            if (((PlayerDropItemEvent)event).getPlayer().getWorld().equals(SpawnStorage.getStorage().getLocation().getWorld())) {
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

    private void loadCommands(SlimePlugin slimePlugin) {
        CommandStorage.register(new MapCreatorCommand(slimePlugin, this, new MapCreatorData()), "map");
        CommandStorage.register(new GameCommand(this), "game");
    }

    public static EggwarsPlugin getInstance() {
        return plugin;
    }
}