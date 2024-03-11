package lc.eggwars.inventory.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.IntegerUtils;

public final class TeamSelectorInventory {

    public void handle(final InventoryClickEvent event, final GameInProgress game, final PreGameTemporaryData data) {
        final BaseTeam team = data.getTeam(event.getSlot());
        event.getWhoClicked().sendMessage("CLICKEADO PAPU");
        if (team == null) {
            event.getWhoClicked().sendMessage("NULO");
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
        final int maxPersonsPerTeam = IntegerUtils.aproximate(
            game.getPlayers().size(),
            game.getMapData().getSpawns().keySet().size());

        return game.getPlayersInTeam().get(team).size() > maxPersonsPerTeam;
    }
}