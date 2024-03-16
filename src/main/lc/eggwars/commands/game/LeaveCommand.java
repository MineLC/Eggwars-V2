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
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;

public final class LeaveCommand implements Command {

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
            }
            break;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            send(player, "Actualmente no estás en ningun juego");
            return;
        }
        player.teleport(SpawnStorage.getStorage().location());
        player.setGameMode(GameMode.ADVENTURE);

        GameStorage.getStorage().leave(game, player, true);
        SpawnStorage.getStorage().setItems(player);
        SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);
        send(player, "Has salido del juego");
    }
}