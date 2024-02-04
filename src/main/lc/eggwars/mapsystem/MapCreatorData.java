package lc.eggwars.mapsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MapCreatorData {
 
    private final Map<UUID, CreatorData> playersCreatingMap = new HashMap<>();

    public boolean remove(final UUID uuid) {
        return playersCreatingMap.remove(uuid) != null;
    }

    public void put(final UUID uuid, final CreatorData data) {
        playersCreatingMap.put(uuid, data);
    }

    public CreatorData getData(final UUID uuid) {
        return playersCreatingMap.get(uuid);
    }
}