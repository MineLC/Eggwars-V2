package lc.eggwars.mapsystem;

import org.bukkit.Material;
import org.bukkit.entity.Player;

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
    public void onClick(Player player) {
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null) {
            return;
        }
        final BaseTeam teamToBreakEgg = map.getPlayersPerTeam().get(player);
        if (team.equals(teamToBreakEgg)) {
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