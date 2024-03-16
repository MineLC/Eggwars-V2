package lc.eggwars.inventory.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;

public final class TeamSelectorInventory {

    public void handle(final InventoryClickEvent event, final GameInProgress game, final PreGameTemporaryData data) {
        final BaseTeam team = data.getTeam(event.getSlot());
        if (team == null) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();

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
        final GameTeam gameTeam = game.getTeamPerBase().get(team);
        if (gameTeam == null) {
            return true;
        }
        if (game.getTeams().size() == 1) {
            return false;
        }
        return gameTeam.getPlayers().size() < game.getMapData().getMaxPersonsPerTeam();
    }
}