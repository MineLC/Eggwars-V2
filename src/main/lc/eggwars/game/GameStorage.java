package lc.eggwars.game;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.game.countdown.types.PreGameCountdown;
import lc.eggwars.game.managers.EggsManager;
import lc.eggwars.game.managers.GeneratorManager;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.spawn.SpawnStorage;
import lc.eggwars.teams.BaseTeam;

import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public final record GameStorage(EggwarsPlugin plugin, PreGameCountdown.Data pregameData, Map<UUID, GameInProgress> playersInGame) {
    private static GameStorage storage;

    public void join(final World world, final GameInProgress map, final Player player) {
        if (map.getState() != GameState.NONE) {
            map.getPlayers().add(player);
            playersInGame.put(player.getUniqueId(), map);
            return;
        }

        new GeneratorManager().setGeneratorSigns(map);
        new EggsManager().setEggs(map);

        map.getPlayers().add(player);
        playersInGame.put(player.getUniqueId(), map);
        map.setState(GameState.PREGAME);

        final PreGameCountdown countdown = new PreGameCountdown(
            pregameData, 
            map.getPlayers(),
            () -> { // Countdown complete
                map.setState(GameState.IN_GAME);
                new GameStarter().start(world, map);
            },
            () -> {
                unloadGame(map, map.getWorld());
            }
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, countdown, 0, 20).getTaskId();
        countdown.setId(id);
        map.setCountdown(countdown);
    }

    public void leave(final GameInProgress map, final Player player, final World world) {
        playersInGame.remove(player.getUniqueId());
        map.getPlayers().remove(player);
        final BaseTeam team = map.getTeamPerPlayer().get(player);

        if (team != null) {
            team.getTeam().removePlayer(player);
            map.getTeamPerPlayer().remove(player);
            finalKill(map, team, player, true);
        }

        if (map.getPlayers().size() <= 0) {
            if (map.getCountdown() != null) {
                Bukkit.getScheduler().cancelTask(map.getCountdown().getId());
            }
            unloadGame(map, map.getWorld());
        }
        player.getInventory().clear();
        player.getScoreboard().getTeams().forEach( (scoreTeam) -> scoreTeam.removePlayer(player));
    }

    private void unloadGame(final GameInProgress map, final World world) {
        new GeneratorManager().unload(map);
        final Set<Entry<Player, BaseTeam>> playersWithTeams = map.getTeamPerPlayer().entrySet();
        for (final Entry<Player, BaseTeam> playerWithTeam : playersWithTeams) {
            playerWithTeam.getValue().getTeam().removePlayer(playerWithTeam.getKey());
        }
        map.getMapData().setGame(null);
        MapStorage.getStorage().unload(world);
    }

    public void finalKill(final GameInProgress map, final BaseTeam team, final Player player, final boolean quit) {
        map.getPlayersLiving().remove(player);
        final Set<Player> players = map.getPlayersInTeam().get(team);

        if (quit) {
            final StatsEggWars stats = Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();
            stats.setLoose(stats.getLoose() + 1);
            stats.setDeaths(stats.getDeaths() + 1);
            stats.setLastDeath(stats.getLastDeath() + 1);
            players.remove(player);
        }

        if (players.size() >= 1) {
            if (!map.getTeamsWithEgg().contains(team)) {
                SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(map.getPlayers());
            }
            return;
        }

        Messages.sendNoGet(map.getPlayers(), Messages.get("team.death").replace("%team%", team.getName()));
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(map.getPlayers());

        if (map.getTeamsWithEgg().size() > 1) {
            return;
        }

        BaseTeam finalTeam = null;
        boolean anotherTeamLive = false;

        for (final Player livingPlayer : map.getPlayersLiving()) {
            if (finalTeam == null) {
                finalTeam = map.getTeamPerPlayer().get(livingPlayer);
                continue;
            }
            if (!finalTeam.equals(map.getTeamPerPlayer().get(livingPlayer))) {
                anotherTeamLive = true;
                break;
            }
        }

        if (anotherTeamLive) {
            return;
        }
        Messages.sendNoGet(map.getPlayers(), Messages.get("team.win").replace("%team%", finalTeam.getName()));        
        for (final Player livingPlayer : map.getPlayersLiving()) {
            livingPlayer.setGameMode(GameMode.SPECTATOR);
        }
        final Set<Player> winners = map.getPlayersInTeam().get(finalTeam);
        for (final Player winner : winners) {
            final StatsEggWars winnerStats = Jugador.getJugador(winner.getName()).getServerStats().getStatsEggWars();
            winnerStats.setWins(winnerStats.getWins() + 1);
        }

        final GameCountdown countdown = new GameCountdown() {
            @Override
            public void run() {
                endGame(map);                
            }
        };

        int id = plugin.getServer().getScheduler().runTaskLater(
            plugin,
            countdown,
            plugin.getConfig().getInt("win-celebration-duration-in-seconds") * 20).getTaskId();

        countdown.setId(id);
        map.setCountdown(countdown);
    }

    private void endGame(final GameInProgress map) {
        for (final Player player : map.getPlayers()) {
            player.teleport(SpawnStorage.getStorage().location());
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            SpawnStorage.getStorage().setItems(player);
        };
        unloadGame(map, map.getWorld());
    }

    public GameInProgress getGame(UUID uuid) {
        return playersInGame.get(uuid);
    }

    public static GameStorage getStorage() {
        return storage;
    }

    final static void update(final GameStorage newStorage) {
        storage = newStorage;
    }
}