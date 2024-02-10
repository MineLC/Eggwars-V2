package lc.eggwars.game;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
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
        final int teamsAmount = map.getSpawns().keySet().size();
        int maxPersonsPerTeam = map.getPlayers().size() / teamsAmount;

        if (maxPersonsPerTeam == 0) {
            maxPersonsPerTeam = 1;
        }

        final Set<Entry<BaseTeam, BlockLocation>> teams = map.getSpawns().entrySet();
        final Map<BaseTeam, Integer> personsPerTeam = new HashMap<>();

        playerLoop : for (final Player player : map.getPlayers()) {
            final BaseTeam playerTeam = map.getTeamPerPlayer().get(player);

            for (final Entry<BaseTeam, BlockLocation> team : teams) {
                final Integer amountPersons = personsPerTeam.get(team.getKey());

                if (amountPersons != null && amountPersons > maxPersonsPerTeam) {
                    continue;
                }

                Set<Player> players = map.getPlayersInTeam().get(team.getKey());

                if (players == null) {
                    players = new HashSet<>();
                    map.getPlayersInTeam().put(team.getKey(), players);
                }
        
                if (players.size() == map.getMaxPersonsPerTeam()) {
                    continue;
                }

                if (playerTeam == null) {
                    player.sendMessage("Se ha añadido al equipo " + team.getKey().getKey());
                    map.getTeamPerPlayer().put(player, team.getKey());
                    team.getKey().getTeam().addPlayer(player);
                    addToTeam(amountPersons, team.getValue(), playerTeam, player, personsPerTeam);
                    continue playerLoop;
                }

                if (playerTeam.equals(team.getKey())) {
                    addToTeam(amountPersons, team.getValue(), playerTeam, player, personsPerTeam);
                    continue playerLoop;
                }

                continue playerLoop;
            }
        }
    }

    private void addToTeam(final Integer amount, final BlockLocation spawnTeam, final BaseTeam team, final Player player, Map<BaseTeam, Integer> personsPerTeam) {
        player.teleport(new Location(player.getWorld(), spawnTeam.x(), spawnTeam.y(), spawnTeam.z()));
        player.setGameMode(GameMode.SURVIVAL);

        if (amount == null) {
            personsPerTeam.put(team, 1);
            return;
        }
        personsPerTeam.replace(team, amount + 1);
    }
}