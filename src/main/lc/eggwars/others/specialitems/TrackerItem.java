package lc.eggwars.others.specialitems;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.GameTeam;

public final class TrackerItem {

    public void handle(final Player player, final GameInProgress game) {
        final GameTeam playerTeam = game.getTeamPerPlayer().get(player);
        final Set<GameTeam> teams = game.getTeams();

        for (final GameTeam team : teams) {
            if (team.getBase().equals(playerTeam.getBase())) {
                continue;
            }
            final Set<Player> teamPlayers = team.getPlayers();

            for (final Player teamPlayer : teamPlayers) {
                if (teamPlayer.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }
                final float distance = (float)teamPlayer.getLocation().distance(player.getLocation());
                player.sendMessage(Messages.get("special-items.tracker")
                    .replace("%distance%", String.valueOf(distance))
                    .replace("%player%", teamPlayer.getName()));

                player.setCompassTarget(teamPlayer.getLocation());
            }
        }
    }
}