package lc.eggwars.game;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.managers.GeneratorManager;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;
import lc.eggwars.utils.BlockLocation;

final class GameStartAndStop {

    void start(final EggwarsPlugin plugin, final GameInProgress game, final String worldName) {
        MapStorage.getStorage().load(worldName).thenAccept((none) -> {
            final World world = Bukkit.getWorld(worldName);
            game.setWorld(world);

            MapStorage.getStorage().loadClickableBlocks(world);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                startForPlayers(game);
                new GeneratorManager().load(game);
            });

            game.startTime();
            game.setState(GameState.IN_GAME);
            game.setCountdown(null);
        });
    }

    private void startForPlayers(final GameInProgress game) {
        setTeams(game);

        final Set<Player> players = game.getPlayers();
        final ShopKeeperManager shopKeeperManager = new ShopKeeperManager();
        for (final Player player : players) {
            KitStorage.getStorage().setKit(player, true);
            shopKeeperManager.send(player, game);
        }

        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());
    }

    void stop(final GameInProgress game) {       
        if (game.getCountdown() != null) {
            Bukkit.getScheduler().cancelTask(game.getCountdown().getId());
        }
        CompletableFuture.runAsync(() -> {
            final World world = game.getWorld();
            new GeneratorManager().unload(game);
            game.getMapData().setGame(null);

            MapStorage.getStorage().unload(world);
            System.gc();
        });
    }

    private void setTeams(final GameInProgress game) {
        final Set<BaseTeam> teams = game.getMapData().getSpawns().keySet();
        final Set<Player> players = game.getPlayers();

        for (final Player player : players) {
            final GameTeam playerTeam = game.getTeamPerPlayer().get(player);

            if (playerTeam != null) {
                addToTeam(game, player, playerTeam, true);
                continue;
            }

            int teamWithMinorPlayers = teams.size();
            GameTeam teamToJoin = null;
    
            for (final BaseTeam team : teams) {
                GameTeam gameTeam = game.getTeamPerBase().get(team);

                if (gameTeam == null) {
                    gameTeam = new GameTeam(team);
                    game.getTeams().add(gameTeam);
                    game.getTeamPerBase().put(team, gameTeam);
                    teamToJoin = gameTeam;
                    break;    
                }
                if (gameTeam.getPlayers().size() < teamWithMinorPlayers) {
                    teamToJoin = gameTeam;
                    continue;
                }
            }

            addToTeam(game, player, teamToJoin, false);
            player.sendMessage("Uniendote al equipo " + teamToJoin.getBase().getName());
        }
    }

    private void addToTeam(final GameInProgress game, final Player player, final GameTeam team, final boolean alreadyInTheTeam) {
        final BlockLocation spawn = game.getMapData().getSpawns().get(team.getBase());
        player.teleport(new Location(game.getWorld(), spawn.x(), spawn.y(), spawn.z()));
        player.setGameMode(GameMode.SURVIVAL);

        if (!alreadyInTheTeam) {
            game.getTeamPerPlayer().put(player, team);
        }

        team.add(player);
    }
}