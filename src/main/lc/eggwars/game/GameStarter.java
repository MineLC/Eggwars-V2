package lc.eggwars.game;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;
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
        setEggsToTeams(map);

        final int teamsAmount = map.getSpawns().keySet().size();
        int maxPersonsPerTeam = map.getPlayers().size() / teamsAmount;

        if (maxPersonsPerTeam == 0) {
            maxPersonsPerTeam = 1;
        }

        final Set<Entry<BaseTeam, BlockLocation>> teams = map.getSpawns().entrySet();
        final Map<BaseTeam, Integer> personsPerTeam = new HashMap<>();

        for (final Player player : map.getPlayers()) {
            final BaseTeam playerTeam = map.getPlayersPerTeam().get(player);

            for (final Entry<BaseTeam, BlockLocation> team : teams) {
                final Integer amountPersons = personsPerTeam.get(team.getKey());

                if (amountPersons != null && amountPersons == maxPersonsPerTeam) {
                    continue;
                }

                if (amountPersons == null) {
                    personsPerTeam.put(playerTeam, 1);
                } else {
                    personsPerTeam.replace(playerTeam, amountPersons + 1);
                }

                if (playerTeam == null || playerTeam.equals(team.getKey())) {
                    map.getPlayersPerTeam().put(player, playerTeam);
                    team.getKey().getTeam().addPlayer(player);

                    final BlockLocation spawnTeam = team.getValue();
                    player.teleport(new Location(world, spawnTeam.x(), spawnTeam.y(), spawnTeam.z()));
                    player.setGameMode(GameMode.SURVIVAL);
                    continue;
                }
            }
        }
    }

    private void setEggsToTeams(final GameMap map) {
        final Set<BaseTeam> teams = map.getSpawns().keySet();
        for (final BaseTeam team : teams) {
            map.getTeamsWithEggs().add(team);
        }
    }
}