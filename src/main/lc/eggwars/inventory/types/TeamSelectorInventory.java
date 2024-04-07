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
        final GameTeam playerTeam = game.getTeamPerPlayer().get(player);
    
        if (playerTeam != null) {
            if (playerTeam.getBase().equals(team)) {
                Messages.send(player, "team.already");
                return;
            }
        }
        if (teamIsFull(game, data, team)) {
            Messages.send(player, "team.full");
            return;
        }
        if (game.getTeams().size() == 1 && (team.equals(game.getTeams().iterator().next().getBase()))) {
            Messages.send(player, "team.balance");
            return;
        }
        removeFromOldTeam(playerTeam, player, game);
        data.joinToTeam(playerTeam, player, game, team);
        player.sendMessage(Messages.get("team.join").replace("%team%", team.getKey()));
    }

    private boolean teamIsFull(final GameInProgress game, final PreGameTemporaryData data, final BaseTeam team) {
        final GameTeam gameTeam = game.getTeamPerBase().get(team);
        if (gameTeam == null) {
            return false;
        }
        return gameTeam.getPlayers().size() >= game.getMapData().getMaxPersonsPerTeam();
    }
    private void removeFromOldTeam(final GameTeam playerTeam, final Player player, final GameInProgress game) {
        playerTeam.remove(player);
        game.getTeamPerPlayer().remove(player);
        if (playerTeam.getPlayers().isEmpty()) {
            game.getTeams().remove(playerTeam);
        }
        player.getInventory().setChestplate(null);
    }
}