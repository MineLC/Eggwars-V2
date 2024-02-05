package lc.eggwars.game;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class GameStarter {

    void start(final World world, final GameMap map) {
        final Set<Entry<Player, BaseTeam>> entries = map.getPlayersPerTeam().entrySet();

        for (Entry<Player, BaseTeam> entry : entries) {
            final Player player = entry.getKey();
            final BlockLocation spawnTeam = map.getSpawn(entry.getValue());

            player.teleport(new Location(world, spawnTeam.x(), spawnTeam.y(), spawnTeam.z()));
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
}