package lc.eggwars.mapsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lc.eggwars.generators.GeneratorData;
import lc.eggwars.generators.TemporaryGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

public final class CreatorData {

    private final Map<String, List<GeneratorData>> generatorsPerIdentifier = new HashMap<>();
    private final Map<BlockLocation, GeneratorData> generators = new HashMap<>();

    private final Map<BaseTeam, BlockLocation> teamEggs = new HashMap<>();

    private final Map<BaseTeam, BlockLocation> spawns = new HashMap<>();

    public boolean addGenerator(final GeneratorData generator) {
        if (generators.get(generator.getLocation()) != null) {
            return false;
        }
        generators.put(generator.getLocation(), generator);
        List<GeneratorData> generators = generatorsPerIdentifier.get(generator.getBase().key());
        if (generators == null) {
            generators = new ArrayList<>();
            generatorsPerIdentifier.put(generator.getBase().key(), generators);
        }
        generators.add(generator);
        return true;
    }

    public boolean removeGenerator(final BlockLocation location) {
        final GeneratorData generator = generators.remove(location);
        if (generator != null) {
            generatorsPerIdentifier.remove(generator.getBase().key());
            return true;
        }
        return false;
    }

    public boolean alreadyExistGenerator(final BlockLocation location) {
        return generators.containsKey(location);
    }

    public void setSpawn(final BaseTeam team, final BlockLocation location) {
        if (spawns.containsKey(team)) {
            spawns.replace(team, location);
            return;
        }
        spawns.put(team, location);
    }

    public void setEgg(final BaseTeam team, final BlockLocation location) {
        if (teamEggs.containsKey(team)) {
            teamEggs.replace(team, location);
            return;
        }
        teamEggs.put(team, location);
    } 

    public Map<BaseTeam, BlockLocation> getSpawnsMap() {
        return spawns;
    }

    public Map<BaseTeam, BlockLocation> getEggsMap() {
        return teamEggs;
    }

    public Map<BlockLocation, GeneratorData> getGeneratorsMap() {
        return generators;
    }

    public Map<String, List<GeneratorData>> getGeneratorsMapPerID() {
        return generatorsPerIdentifier;
    }
}