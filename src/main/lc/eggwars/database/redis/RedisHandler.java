package lc.eggwars.database.redis;

import org.bukkit.configuration.file.FileConfiguration;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

public final class RedisHandler {

    private JedisPooled pool;

    public void init(final FileConfiguration config) throws Exception {
        final String ip = config.getString("redis.ip");
        final int port = config.getInt("redis.port");
        if (ip == null || port == 0) {
            throw new Exception("Connection to redis is invalid, ip or port is null");
        }
        final String password = config.getString("redis.password");
        final String user = config.getString("redis.user");

        final HostAndPort node = new HostAndPort(ip, port);
        final JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
            .resp3()
            .user(user)
            .password(password)
            .build();

        pool = new JedisPooled(node, clientConfig);

        RedisManager.update(new RedisManager(pool));
    }

    public void shutdown() {
        pool.close();
    }
}