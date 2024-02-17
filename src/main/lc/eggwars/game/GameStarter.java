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

import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class GameStarter {

    void start(final World world, final GameMap map) {
        setTeams(world, map);
        setShopkeepersId(map);
        new ShopKeeperManager().send(map.getPlayers(), world, map);
    }

    private void setShopkeepersId(final GameMap map) {
        for (int i = 0; i < map.getShopIDs().length; i++) {
            map.getShopIDs()[i] = Integer.MAX_VALUE - i;
        }
    }

    private void setTeams(final World world, final GameMap map) {
        final int teamsAmount = map.getSpawns().keySet().size();
        int maxPersonsPerTeam = map.getPlayers().size() / teamsAmount;

        if (maxPersonsPerTeam == 0) {
            maxPersonsPerTeam = 1;
        }

        final Set<Entry<BaseTeam, BlockLocation>> teams = map.getSpawns().entrySet();
        final Map<BaseTeam, Integer> personsPerTeam = new HashMap<>();

        playerLoop : for (final Player player : map.getPlayers()) {
            final BaseTeam playerTeam = map.getTeamPerPlayer().get(player);

            for (final Entry<BaseTeam, BlockLocation> entry : teams) {
                final BaseTeam team = entry.getKey();
                final Integer amountPersons = personsPerTeam.get(team);

                if (amountPersons != null && amountPersons >= maxPersonsPerTeam) {
                    continue;
                }

                Set<Player> players = map.getPlayersInTeam().get(team);

                if (players == null) {
                    players = new HashSet<>();
                    map.getPlayersInTeam().put(team, players);
                }
        
                if (players.size() == map.getMaxPersonsPerTeam()) {
                    continue;
                }

                if (playerTeam == null) {
                    map.getTeamPerPlayer().put(player, team);
                    team.getTeam().addPlayer(player);
                    addToTeam(amountPersons, map, entry.getValue(), playerTeam, player, personsPerTeam);
                    player.sendMessage(Messages.get("team.join").replace("%team%", team.getName()));
                    continue playerLoop;
                }

                if (playerTeam.equals(team)) {
                    addToTeam(amountPersons, map, entry.getValue(), playerTeam, player, personsPerTeam);
                    continue playerLoop;
                }

                continue playerLoop;
            }
        }
    }

    private void addToTeam(final Integer amount, final GameMap map, final BlockLocation spawnTeam, final BaseTeam team, final Player player, Map<BaseTeam, Integer> personsPerTeam) {
        player.teleport(new Location(player.getWorld(), spawnTeam.x(), spawnTeam.y(), spawnTeam.z()));
        player.setGameMode(GameMode.SURVIVAL);
        map.getPlayersLiving().add(player);

        if (amount == null) {
            personsPerTeam.put(team, 1);
            map.getTeamsWithEgg().add(team);
            return;
        }
        personsPerTeam.replace(team, amount + 1);
    }
}