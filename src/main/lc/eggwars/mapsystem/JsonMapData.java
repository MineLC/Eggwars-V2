package lc.eggwars.mapsystem;

import java.util.Map;

public class JsonMapData {
    private String world;
    private final int maxPersonsPerTeam;
    private final int borderSize;
    private final Map<String, String> spawns;
    private final Map<String, String[]> generators;
    private final Map<String, String> teamEggs;
    private final String[] shopspawns;

    public JsonMapData(
        String world,
        int maxPersonsPerTeam, int borderSize,
        Map<String, String> spawns, Map<String, String[]> generators,
        Map<String, String> teamEggs, String[] shopSpawns
    ) {
        this.world = world;
        this.maxPersonsPerTeam = maxPersonsPerTeam;
        this.borderSize = borderSize;
        this.spawns = spawns;
        this.generators = generators;
        this.teamEggs = teamEggs;
        this.shopspawns = shopSpawns;
    }

    public String world() {
        return world;
    }

    public int maxPersonsPerTeam() {
        return maxPersonsPerTeam;
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

    public Map<String, String> teamEggs() {
        return teamEggs;
    }

    public String[] shopSpawns() {
        return shopspawns;
    }
}