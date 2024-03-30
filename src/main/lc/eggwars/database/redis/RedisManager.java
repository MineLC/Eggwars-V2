package lc.eggwars.database.redis;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.mapsystem.MapData;
import redis.clients.jedis.JedisPooled;

public final class RedisManager {
    private static RedisManager manager;

    private final JedisPooled pool;

    RedisManager(JedisPooled pool) {
        this.pool = pool;
    }

    public void updateGame(final GameInProgress game) {
        CompletableFuture.runAsync(() -> pool.hset("eggwars", game.getMapData().toString(), serializateGame(game)));
    }

    public void resetGame(final MapData map) {
        map.setGame(new GameInProgress(map));
        CompletableFuture.runAsync(() -> pool.hset("eggwars", map.toString(), "0|0|NONE"));
    }

    public void resetGames(final Map<String, String> data) {
        CompletableFuture.runAsync(() -> {
            pool.hset("eggwars", data);
        });
    }

    public String serializateGame(final GameInProgress game) {
        final StringBuilder builder = new StringBuilder(game.getPlayers().size() + 20);
        builder.append(game.getPlayers().size());
        builder.append('|');
        builder.append(game.getStartedTime());
        builder.append('|');
        builder.append(game.getState().toString());
        return builder.toString();
    }

    public static RedisManager getManager() {
        return manager;
    }

    static void update(RedisManager newManager) {
        manager = newManager;
    }
}