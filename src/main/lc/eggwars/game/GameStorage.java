package lc.eggwars.game;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        player.setGameMode(GameMode.SPECTATOR);

        if (map.getState() == GameState.IN_GAME) {
            return;
        }

        if (map.getState() == GameState.PREGAME) {
            map.getPlayers().add(player);
            playersInGame.put(player.getUniqueId(), map);
            return;
        }

        // add items
        map.resetPlayersData();

        final PreGameCountdown countdown = new PreGameCountdown(
            preGameData, 
            map.getPlayers(),
            () -> { // Countdown complete
                gamesStarted.add(map);
                map.setState(GameState.IN_GAME);
                new GameStarter().start(world, map);
            },
            () -> { // Game canceled
                gamesStarted.remove(map);
                Bukkit.unloadWorld(world, false);
                map.setState(GameState.NONE);
                map.resetPlayersData();
            }
        );

        countdown.setId(plugin.getServer().getScheduler().runTaskTimer(plugin, countdown, 0, 20).getTaskId());
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