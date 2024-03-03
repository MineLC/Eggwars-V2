package lc.eggwars.game;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lc.eggwars.game.managers.EggsManager;
import lc.eggwars.game.managers.GeneratorManager;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class GameManager {

    void start(final GameInProgress map) {
        map.setState(GameState.IN_GAME);
        map.setCountdown(null);

        setTeams(map);

        for (final Player player : map.getPlayers()) {
            KitStorage.getStorage().setKit(player, true);
        }

        new GeneratorManager().load(map);
        new EggsManager().setEggs(map);
        new ShopKeeperManager().send(map.getPlayers(), map.getWorld(), map);

        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(map.getPlayers());
    }

    void stop(final GameInProgress game) {       
        game.setGameFinished(true);
        if (game.getCountdown() != null) {
            Bukkit.getScheduler().cancelTask(game.getCountdown().getId());
        }

        new GeneratorManager().unload(game);
        MapStorage.getStorage().unload(game.getWorld());
        game.getMapData().setGame(null);
    }

    private void setTeams(final GameInProgress game) {
        final int teamsAmount = game.getMapData().getSpawns().keySet().size();
        int maxPersonsPerTeam = game.getPlayers().size() / teamsAmount;

        if (maxPersonsPerTeam == 0) {
            maxPersonsPerTeam = 1;
        }

        final Set<Entry<BaseTeam, BlockLocation>> teams = game.getMapData().getSpawns().entrySet();
        final Map<BaseTeam, Integer> personsPerTeam = new HashMap<>();

        playerLoop : for (final Player player : game.getPlayers()) {
            final BaseTeam playerTeam = game.getTeamPerPlayer().get(player);

            for (final Entry<BaseTeam, BlockLocation> entry : teams) {
                final BaseTeam team = entry.getKey();
                final Integer amountPersons = personsPerTeam.get(team);

                if (amountPersons != null && amountPersons >= maxPersonsPerTeam) {
                    continue;
                }

                Set<Player> players = game.getPlayersInTeam().get(team);

                if (players == null) {
                    players = new HashSet<>();
                    game.getPlayersInTeam().put(team, players);
                }
        
                if (players.size() == game.getMapData().getMaxPersonsPerTeam()) {
                    continue;
                }

                if (playerTeam == null) {
                    game.getTeamPerPlayer().put(player, team);
                    team.getTeam().addPlayer(player);
                    addToTeam(amountPersons, game, entry.getValue(), playerTeam, player, personsPerTeam);
                    player.sendMessage(Messages.get("team.join").replace("%team%", team.getName()));
                    continue playerLoop;
                }

                if (playerTeam.equals(team)) {
                    addToTeam(amountPersons, game, entry.getValue(), playerTeam, player, personsPerTeam);
                    continue playerLoop;
                }

                continue playerLoop;
            }
        }
    }

    private void addToTeam(final Integer amount, final GameInProgress map, final BlockLocation spawnTeam, final BaseTeam team, final Player player, Map<BaseTeam, Integer> personsPerTeam) {
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