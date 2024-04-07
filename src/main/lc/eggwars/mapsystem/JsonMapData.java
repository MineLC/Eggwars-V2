package lc.eggwars.mapsystem;

import java.util.Map;

public record JsonMapData(
    String world,
    String name,
    int maxPersonsPerTeam,
    int borderSize,
    Map<String, String> spawns,
    Map<String, String[]> generators,
    Map<String, String> teamEggs,
    String[] shopspawns
) {
}