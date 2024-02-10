package lc.eggwars.game;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.types.PreGameCountdown;
import lc.eggwars.generators.GeneratorManager;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.spawn.SpawnStorage;
import lc.eggwars.teams.BaseTeam;

public final class GameStorage {
    private static GameStorage storage;

    private final PreGameCountdown.Data preGameData;
    private final EggwarsPlugin plugin;

    private final Map<UUID, GameMap> playersInGame = new HashMap<>();

    GameStorage(EggwarsPlugin plugin, PreGameCountdown.Data preGameData) {
        this.plugin = plugin;
        this.preGameData = preGameData;
    }

    public void join(final World world, final GameMap map, final Player player) {
        if (map.getState() == GameState.IN_GAME || map.getState() == GameState.PREGAME) {
            map.getPlayers().add(player);
            playersInGame.put(player.getUniqueId(), map);
            return;
        }

        // Replace this comentary with a system to add items to vote, select team, and kits (Comming soon)

        map.resetData();
        map.getPlayers().add(player);
        playersInGame.put(player.getUniqueId(), map);

        map.setState(GameState.PREGAME);
        map.setWorld(world);

        final PreGameCountdown countdown = new PreGameCountdown(
            preGameData, 
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
        map.setTaskId(id);
    }

    public void leave(final GameMap map, final Player player, final World world) {
        playersInGame.remove(player.getUniqueId());
        map.getPlayers().remove(player);
        final BaseTeam team = map.getTeamPerPlayer().get(player);

        if (team != null) {
            team.getTeam().removePlayer(player);
            map.getTeamPerPlayer().remove(player);
        }

        if (map.getPlayers().size() <= 0) {
            if (map.getTaskId() != -1) {
                Bukkit.getScheduler().cancelTask(map.getTaskId());
            }

            unloadGame(map, map.getWorld());
        }
    }

    public void finalDeath(final GameMap map, final Player player) {
        if (map.getPlayers().size() - 1 <= 0) {
            if (map.getTaskId() != -1) {
                Bukkit.getScheduler().cancelTask(map.getTaskId());
            }

            player.teleport(SpawnStorage.getStorage().getLocation());
            player.setGameMode(GameMode.ADVENTURE);

            unloadGame(map, map.getWorld());
            return;
        }
        map.getPlayers().remove(player);
    }

    private void unloadGame(final GameMap map, final World world) {
        new GeneratorManager().unload(map);

        final Set<Entry<Player, BaseTeam>> playersWithTeams = map.getTeamPerPlayer().entrySet();
        for (final Entry<Player, BaseTeam> playerWithTeam : playersWithTeams) {
            playerWithTeam.getValue().getTeam().removePlayer(playerWithTeam.getKey());
        }

        map.setState(GameState.NONE);
        map.resetData();
        map.setWorld(null);

        MapStorage.getStorage().unload(world);
        System.gc();
    }

    public GameMap getGame(UUID uuid) {
        return playersInGame.get(uuid);
    }

    public static GameStorage getStorage() {
        return storage;
    }

    final static void update(final GameStorage newStorage) {
        storage = newStorage;
    }
}