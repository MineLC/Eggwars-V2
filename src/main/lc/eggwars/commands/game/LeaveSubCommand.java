package lc.eggwars.commands.game;

import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.eggwars.commands.BasicSubCommand;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.spawn.SpawnStorage;

final class LeaveSubCommand implements BasicSubCommand {

    @Override
    public void execute(Player player, String[] args) {
        final Set<Entry<String, Set<Player>>> playersWaitingToJoin = MapStorage.getStorage().getWorldsCurrentlyLoading().entrySet();

        for (final Entry<String, Set<Player>> entry : playersWaitingToJoin) {
            final Set<Player> players = entry.getValue();
            if (!players.contains(player)) {
                continue;
            }
            players.remove(player);
            if (players.size() <= 0) {
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

        player.teleport(SpawnStorage.getStorage().getLocation());
        player.setGameMode(GameMode.ADVENTURE);

        GameStorage.getStorage().leave(map, player, map.getWorld());
        SpawnStorage.getStorage().setItems(player);
        send(player, "Has salido del juego");
    }
}