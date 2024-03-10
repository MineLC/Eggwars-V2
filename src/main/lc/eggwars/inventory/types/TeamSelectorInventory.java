package lc.eggwars.inventory.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.BaseTeam;

public final class TeamSelectorInventory {

    public void handle(final InventoryClickEvent event, final GameInProgress game) {
        if (!(game.getCountdown() instanceof PreGameCountdown pregrame)) {
            return;
        }
        final BaseTeam team = pregrame.getTemporaryData().getTeam(event.getSlot());
        if (team == null) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        final PreGameTemporaryData data = pregrame.getTemporaryData();

        if (!canJoin(game, data, team)) {
            Messages.send(player, "team.full");
            return;
        }
        if (!data.joinToTeam(player, game, team)) {
            Messages.send(player, "team.already");
            return;
        }
        player.sendMessage(Messages.get("team.join").replace("%team%", team.getKey()));
    }

    private boolean canJoin(final GameInProgress game, final PreGameTemporaryData data, final BaseTeam team) {
        final int maxPersonsPerTeam = getMaxPlayers(
            game.getPlayers().size(),
            game.getMapData().getSpawns().keySet().size());

        return game.getPlayersInTeam().get(team).size() > maxPersonsPerTeam;
    }

    private int getMaxPlayers(int players, int teamsAmount) {
        if (players <= teamsAmount) {
            return 1;
        }
        if (players % teamsAmount == 0) {
            return players / teamsAmount;
        }
        return (players % teamsAmount == 0)
            ? players / teamsAmount
            : (players / teamsAmount) + 1;
    }
}