package lc.eggwars.commands.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.commands.SubCommand;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.mapsystem.MapStorage;

final class JoinSubCommand implements SubCommand {

    private final EggwarsPlugin plugin;

    JoinSubCommand(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            send(sender, "&cFormat: /join &7(worldname)");
            return;
        }

        final GameMap map = MapStorage.getStorage().getMap(args[1]);
        if (map == null) {
            sender.sendMessage("Este mapa no existe");
            return;
        }
        final Player player = (Player)sender;
            
        if (map.getState() != GameState.NONE) {
            final World world = Bukkit.getWorld(args[1]);
            if (world == null) {
                send(sender, "&cError on load the world");
                return;
            }
            GameStorage.getStorage().join(world, map, player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(world.getSpawnLocation());
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final World world = MapStorage.getStorage().load(args[1]);
            if (world == null) {
                send(sender, "&cError on load the world");
                return;
            }
            GameStorage.getStorage().join(world, map, player);
            player.sendMessage("Teleporting to " + world.getName());
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(world.getSpawnLocation());
            });
        });
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}