package lc.eggwars.mapsystem;

import java.util.Map;

public class JsonMapData {
    private final String worldName;
    private final Map<String, String[]> spawns;
    private final Map<String, String[]> generators;

    public JsonMapData(String worldName, Map<String, String[]> generators, Map<String, String[]> spawns){
        this.worldName = worldName;
        this.spawns = spawns;
        this.generators = generators;
    }

    public String getWorldName() {
        return worldName;
    }

    public Map<String, String[]> getGenerators() {
        return generators;
    }

    public Map<String, String[]> getSpawns() {
        return spawns;
    }
}