package lc.eggwars.mapsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import lc.eggwars.generators.SignGenerator;
import lc.eggwars.teams.BaseTeam;

public final class CreatorData {
    private final Map<String, List<SignGenerator>> generatorsPerIdentifier = new HashMap<>();
    private final Map<Location, SignGenerator> generators = new HashMap<>();

    private final Map<BaseTeam, Set<Location>> spawns = new HashMap<>();

    public boolean addGenerator(final SignGenerator generator) {
        if (generators.get(generator.getLocation()) != null) {
            return false;
        }
        generators.put(generator.getLocation(), generator);
        List<SignGenerator> generators = generatorsPerIdentifier.get(generator.getBase().key());
        if (generators == null) {
            generators = new ArrayList<>();
            generatorsPerIdentifier.put(generator.getBase().key(), generators);
        }
        generators.add(generator);
        return true;
    }

    public boolean removeGenerator(final Location location) {
        final SignGenerator generator = generators.remove(location);
        if (generator != null) {
            generatorsPerIdentifier.remove(generator.getBase().key());
            return true;
        }
        return false;
    }

    public boolean alreadyExistGenerator(final Location location) {
        return generators.containsKey(location);
    }

    public boolean addSpawn(final BaseTeam team, final Location location) {
        Set<Location> listOfSpawns = spawns.get(team);
        if (listOfSpawns == null) {
            listOfSpawns = new HashSet<>();
            spawns.put(team, listOfSpawns);
        }
        if (listOfSpawns.contains(location)) {
            return false;
        }
        listOfSpawns.add(location);
        return true;
    }

    public Map<BaseTeam, Set<Location>> getSpawnsMap() {
        return spawns;
    }

    public Map<Location, SignGenerator> getGeneratorsMap() {
        return generators;
    }

    public Map<String, List<SignGenerator>> getGeneratorsMapPerID() {
        return generatorsPerIdentifier;
    }
}