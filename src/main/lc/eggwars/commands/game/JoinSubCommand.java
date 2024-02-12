package lc.eggwars.commands.game;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.commands.SubCommand;
import lc.eggwars.game.GameMap;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.managers.EggsManager;
import lc.eggwars.game.managers.GeneratorManager;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.players.PlayerStorage;

final class JoinSubCommand implements SubCommand {

    private final EggwarsPlugin plugin;

    JoinSubCommand(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            send(player, "&cFormat: /join &7(worldname)");
            return;
        }

        final GameMap map = MapStorage.getStorage().getMap(args[1]);
        if (map == null) {
            player.sendMessage("Este mapa no existe");
            return;
        }

        if (map.getState() == GameState.PREGAME) {
            if (map.getPlayers().size() >= map.getMaxPersonsPerTeam()) {
                send(player, "Este mapa ya está lleno");
                return;
            }
        }

        if (map.getState() != GameState.NONE) {
            GameStorage.getStorage().join(map.getWorld(), map, player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(map.getWorld().getSpawnLocation());
            new ShopKeeperManager().send(player, PlayerStorage.getInstance().get(player.getUniqueId()), map);
            return;
        }

        if (map.getState() == GameState.NONE) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                final World world = MapStorage.getStorage().load(args[1]);
                if (world == null) {
                    send(player, "&cError on load the world");
                    return;
                }
                GameStorage.getStorage().join(world, map, player);

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    new GeneratorManager().setGeneratorSigns(map);
                    new EggsManager().setEggs(map);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(map.getWorld().getSpawnLocation());
                });
            });
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.copyOf(MapStorage.getStorage().getMaps().keySet());
    }
}