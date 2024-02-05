package lc.eggwars.spawn;

import org.bukkit.Location;

public final class SpawnStorage {
    private static SpawnStorage storage;
    private final Location location;

    private SpawnStorage(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    final static void update(final Location location) {
        storage = new SpawnStorage(location);
    }

    public static SpawnStorage getStorage() {
        return storage;
    }
}