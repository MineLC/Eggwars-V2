package lc.eggwars.commands.game;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.others.tab.TabStorage;

public final class LeaveCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender; 
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            send(player, "Actualmente no estás en ningun juego");
            return;
        }

        GameStorage.getStorage().leave(game, player, true);
        TabStorage.getStorage().removePlayers(player, game.getPlayers());
        SpawnStorage.getStorage().sendToSpawn(player);
        SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);

        final Set<Player> players = game.getPlayers();
        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }
        TabStorage.getStorage().sendPlayerInfo(player, SpawnStorage.getStorage().getPlayers());
        player.getActivePotionEffects().forEach((potion) -> player.removePotionEffect(potion.getType()));
        send(player, "Has salido del juego");
    }
}