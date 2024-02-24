package lc.eggwars.commands.game;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.lcspigot.commands.Command;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.pregameitems.PregameItemsStorage;
import lc.eggwars.players.PlayerStorage;

final class JoinSubCommand implements Command {

    private final EggwarsPlugin plugin;

    JoinSubCommand(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
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
            if (MapStorage.getStorage().getWorldsCurrentlyLoading().containsKey(args[1])) {
                Messages.send(sender, "map.currently-loading");
                return;
            }

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

        player.getInventory().clear();

        if (game.getState() == GameState.PREGAME) {
            if (game.getPlayers().size() >= (map.getMaxPersonsPerTeam() * map.getSpawns().size())) {
                Messages.send(player, "pregame.full");
                return;
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(game.getWorld().getSpawnLocation());

            PregameItemsStorage.getStorage().send(player);

        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(game.getWorld().getSpawnLocation());  
        }

        GameStorage.getStorage().join(game.getWorld(), game, player);
        new ShopKeeperManager().send(player, PlayerStorage.getStorage().get(player.getUniqueId()), game);
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 1) ? (String[])MapStorage.getStorage().getMaps().keySet().toArray() : none();
    }
}