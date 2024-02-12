package lc.eggwars.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerStorage {
    private static final PlayerStorage INSTANCE = new PlayerStorage();

    private final Map<UUID, PlayerData> data = new HashMap<>();

    public void addPlayer(final UUID uuid, final PlayerData data) {
        this.data.put(uuid, data);
    }

    public void removePlayer(final UUID uuid) {
        data.remove(uuid);
    }

    public PlayerData get(final UUID uuid) {
        return data.get(uuid);
    }

    public static PlayerStorage getInstance() {
        return INSTANCE;
    }
}