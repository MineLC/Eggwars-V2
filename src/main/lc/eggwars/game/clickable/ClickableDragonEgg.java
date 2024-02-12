package lc.eggwars.game.clickable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.game.GameMap;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.Chat;
import lc.eggwars.utils.ClickableBlock;

public final class ClickableDragonEgg implements ClickableBlock  {
    private final BaseTeam team;
    private final BlockLocation location;

    public ClickableDragonEgg(BaseTeam team, BlockLocation location) {
        this.team = team;
        this.location = location;
    }

    @Override
    public void onClick(final Player player, final Action action) {
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null || map.getState() != GameState.IN_GAME || !map.getTeamsWithEgg().contains(team)) {
            return;
        }

        final BaseTeam playerTeam = map.getTeamPerPlayer().get(player);
        if (team.equals(playerTeam)) {
            player.sendMessage("No puedes romper tu propio huevo...");
            return;
        }

        map.getTeamsWithEgg().remove(team);
        player.getWorld().getBlockAt(location.x(), location.y(), location.z()).setType(Material.AIR);
        Chat.send("Se ha roto el huevo del equipo " + team.getName(), map.getPlayers());
    }
}