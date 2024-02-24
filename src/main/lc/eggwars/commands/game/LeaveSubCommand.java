package lc.eggwars.commands.game;

import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.spawn.SpawnStorage;

final class LeaveSubCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender; 
        final Set<Entry<String, Set<Player>>> playersWaitingToJoin = MapStorage.getStorage().getWorldsCurrentlyLoading().entrySet();

        for (final Entry<String, Set<Player>> entry : playersWaitingToJoin) {
            final Set<Player> players = entry.getValue();
            if (!players.contains(player)) {
                continue;
            }
            players.remove(player);
            if (players.isEmpty()) {
                final String worldName = entry.getKey();
                MapStorage.getStorage().getWorldsCurrentlyLoading().remove(worldName);
                MapStorage.getStorage().getWorldsThatNeedUnload().add(worldName);
            }
            break;
        }

        final GameInProgress map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null) {
            send(player, "Actualmente no estás en ningun juego");
            return;
        }

        player.teleport(SpawnStorage.getStorage().location());
        player.setGameMode(GameMode.ADVENTURE);

        GameStorage.getStorage().leave(map, player, map.getWorld());
        SpawnStorage.getStorage().setItems(player);
        send(player, "Has salido del juego");
    }
}