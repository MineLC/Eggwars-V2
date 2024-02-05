package lc.eggwars.mapsystem;

import java.util.Map;

public class JsonMapData {
    private String world;
    private final int borderSize;
    private final Map<String, String> spawns;
    private final Map<String, String[]> generators;

    public JsonMapData(String world, int borderSize, Map<String, String> spawns, Map<String, String[]> generators) {
        this.world = world;
        this.borderSize = borderSize;
        this.spawns = spawns;
        this.generators = generators;
    }

    public String world() {
        return world;
    }

    public int borderSize() {
        return borderSize;
    }

    public Map<String, String> spawns() {
        return spawns;
    }

    public Map<String, String[]> generators() {
        return generators;
    }
}