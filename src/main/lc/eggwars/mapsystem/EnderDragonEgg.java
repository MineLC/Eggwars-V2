package lc.eggwars.mapsystem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.Chat;
import lc.eggwars.utils.ClickableBlock;

public final class EnderDragonEgg implements ClickableBlock  {
    private final BaseTeam team;
    private final BlockLocation location;

    EnderDragonEgg(BaseTeam team, BlockLocation location) {
        this.team = team;
        this.location = location;
    }

    @Override
    public void onClick(final Player player, final Action action) {
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null || map.getState() != GameState.IN_GAME) {
            return;
        }
        final BaseTeam playerTeam = map.getTeamPerPlayer().get(player);
        if (team.equals(playerTeam)) {
            player.sendMessage("No puedes romper tu propio huevo...");
            return;
        }

        if (map.getTeamsWithoutEgg().contains(team)) {
            return;
        }

        map.getTeamsWithoutEgg().add(team);
        player.getWorld().getBlockAt(location.x(), location.y(), location.z()).setType(Material.AIR);
        Chat.send("Se ha roto el juego del equipo " + team.getName(), map.getPlayers());
    }
}