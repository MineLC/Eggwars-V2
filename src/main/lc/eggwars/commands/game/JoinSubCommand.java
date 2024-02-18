package lc.eggwars.commands.game;

import java.util.List;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.commands.SubCommand;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.messages.Messages;
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

        final MapData map = MapStorage.getStorage().getMapData(args[1]);
        if (map == null) {
            send(player, "&cThis map don't exist");
            return;
        }

        final GameInProgress game = map.getGameInProgress();

        if (game == null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                final Set<Player> playersWaitingToJoin = MapStorage.getStorage().load(args[1]);
                if (playersWaitingToJoin == null) {
                    send(player, "&cError on load the world");
                    return;
                }
                playersWaitingToJoin.add(player);
            });
            return;
        }

        if (game.getState() == GameState.PREGAME) {
            if (game.getPlayers().size() >= map.getMaxPersonsPerTeam()) {
                Messages.send(player, "pregame.full");
                return;
            }
        }

        player.getInventory().clear();

        GameStorage.getStorage().join(game.getWorld(), game, player);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(game.getWorld().getSpawnLocation());

        new ShopKeeperManager().send(player, PlayerStorage.getInstance().get(player.getUniqueId()), game);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return (args.length == 2) ? List.copyOf(MapStorage.getStorage().getMaps().keySet()) : List.of();
    }
}