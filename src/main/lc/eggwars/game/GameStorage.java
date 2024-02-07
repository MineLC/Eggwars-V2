package lc.eggwars.game;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.types.PreGameCountdown;
import lc.eggwars.mapsystem.GameMap;

public final class GameStorage {
    private static GameStorage storage;

    private final PreGameCountdown.Data preGameData;
    private final EggwarsPlugin plugin;

    private final Map<UUID, GameMap> playersInGame = new HashMap<>();
    private final List<GameMap> gamesStarted = new ArrayList<>();

    GameStorage(EggwarsPlugin plugin, PreGameCountdown.Data preGameData) {
        this.plugin = plugin;
        this.preGameData = preGameData;
    }

    public void join(final World world, final GameMap map, final Player player) {
        if (map.getState() == GameState.IN_GAME) {
            map.getPlayers().add(player);
            return;
        }

        // Replace this comentary with a system to add items to vote, select team, and kits (Comming soon)

        if (map.getState() == GameState.PREGAME) {
            map.getPlayers().add(player);
            playersInGame.put(player.getUniqueId(), map);
            return;
        }

        map.resetData();
        map.getPlayers().add(player);
        playersInGame.put(player.getUniqueId(), map);
        if (map.getState() == GameState.NONE){
            map.setState(GameState.PREGAME);
        }

        if (map.getState() == GameState.NONE){
            map.setState(GameState.PREGAME);
        }

        final PreGameCountdown countdown = new PreGameCountdown(
            preGameData, 
            map.getPlayers(),
            () -> { // Countdown complete
                gamesStarted.add(map);
                map.setState(GameState.IN_GAME);
                new GameStarter().start(world, map);
            },
            () -> {
                unloadGame(map);
                Bukkit.unloadWorld(world, false);
            }
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, countdown, 0, 20).getTaskId();
        countdown.setId(id);
        map.setTaskId(id);
    }

    public void unloadGame(final GameMap map) {
        gamesStarted.remove(map);
        map.setState(GameState.NONE);
        map.resetData();
    }

    public GameMap getGame(UUID uuid) {
        return playersInGame.get(uuid);
    }

    public void remove(UUID uuid) {
        playersInGame.remove(uuid);
    }

    public static GameStorage getStorage() {
        return storage;
    }

    final static void update(final GameStorage newStorage) {
        storage = newStorage;
    }
}