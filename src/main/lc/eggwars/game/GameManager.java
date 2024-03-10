package lc.eggwars.game;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lc.eggwars.game.managers.EggsManager;
import lc.eggwars.game.managers.GeneratorManager;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.IntegerUtils;

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
        if (game.getCountdown() != null) {
            Bukkit.getScheduler().cancelTask(game.getCountdown().getId());
        }
        CompletableFuture.runAsync(() -> {
            new GeneratorManager().unload(game);
            MapStorage.getStorage().unload(game.getWorld());
            game.getMapData().setGame(null);    
        });
    }

    private void setTeams(final GameInProgress game) {
        final Set<BaseTeam> teams = game.getMapData().getSpawns().keySet();
        final Set<Player> players = game.getPlayers();

        final int maxPersonsPerTeam = IntegerUtils.aproximate(players.size(), teams.size());

        for (final Player player : players) {
            final BaseTeam playerTeam = game.getTeamPerPlayer().get(player);

            if (playerTeam != null) {
                addToTeam(game, player, playerTeam, true);
                continue;
            }

            for (final BaseTeam team : teams) {
                Set<Player> teamPlayers = game.getPlayersInTeam().get(team);

                if (teamPlayers == null) {
                    teamPlayers = new HashSet<>();
                    teamPlayers.add(player);
                    game.getPlayersInTeam().put(team, teamPlayers);
                    addToTeam(game, player, team, false);
                    break;
                }

                if (teamPlayers.size() <= maxPersonsPerTeam) {
                    teamPlayers.add(player);
                    addToTeam(game, player, team, false);
                }
                break;
            }
        }
    }

    private void addToTeam(final GameInProgress game, final Player player, final BaseTeam team, final boolean alreadyInThisTeam) {
        final BlockLocation spawn = game.getMapData().getSpawns().get(team);
        player.teleport(new Location(player.getWorld(), spawn.x(), spawn.y(), spawn.z()));
        player.setGameMode(GameMode.SURVIVAL);

        if (!alreadyInThisTeam) {
            game.getTeamPerPlayer().put(player, team);
        }

        team.getTeam().addPlayer(player);

        game.getTeamsWithEgg().add(team);
        game.getPlayersLiving().add(player);
    }
}