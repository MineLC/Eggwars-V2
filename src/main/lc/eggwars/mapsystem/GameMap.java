package lc.eggwars.mapsystem;

import java.util.Map;

import org.bukkit.Location;

import lc.eggwars.generators.SignGenerator;
import lc.eggwars.teams.BaseTeam;

public final class GameMap {
    private final Map<BaseTeam, Location[]> spawns;
    private final SignGenerator[] generators;

    GameMap(SignGenerator[] generators, Map<BaseTeam, Location[]> spawns) {
        this.generators = generators;
        this.spawns = spawns;
    }

    public Location[] getSpawns(final BaseTeam team) {
        return spawns.get(team);
    }
    
    public SignGenerator[] getGenerators() {
        return generators;
    }
}