package lc.eggwars.commands.game;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;

public final class LeaveCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender; 
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

        final Set<Player> players = game.getPlayers();
        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }
        send(player, "Has salido del juego");
    }
}