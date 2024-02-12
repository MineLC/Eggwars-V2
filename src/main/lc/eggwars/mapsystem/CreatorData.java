package lc.eggwars.mapsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.EntityLocation;

public final class CreatorData {

    private final Map<String, List<ClickableSignGenerator>> generatorsPerIdentifier = new HashMap<>();
    private final Map<BlockLocation, ClickableSignGenerator> generators = new HashMap<>();

    private final Map<BaseTeam, BlockLocation> teamEggs = new HashMap<>();

    private final Map<BaseTeam, BlockLocation> spawns = new HashMap<>();
    private final Set<EntityLocation> shopkeeperSpawns = new HashSet<>();

    private int maxPersonsPerTeam = 1;

    public void setMaxPersonsPerTeam(int max) {
        this.maxPersonsPerTeam = max;
    }

    public int getMaxPersonsPerTeam() {
        return maxPersonsPerTeam;
    }

    public boolean addGenerator(final ClickableSignGenerator generator) {
        if (generators.get(generator.getLocation()) != null) {
            return false;
        }
        generators.put(generator.getLocation(), generator);
        List<ClickableSignGenerator> generators = generatorsPerIdentifier.get(generator.getBase().key());
        if (generators == null) {
            generators = new ArrayList<>();
            generatorsPerIdentifier.put(generator.getBase().key(), generators);
        }
        generators.add(generator);
        return true;
    }

    public boolean removeGenerator(final BlockLocation location) {
        final ClickableSignGenerator generator = generators.remove(location);
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

    public Set<EntityLocation> getShopKeepersSpawns() {
        return shopkeeperSpawns;
    }

    public Map<BlockLocation, ClickableSignGenerator> getGeneratorsMap() {
        return generators;
    }

    public Map<String, List<ClickableSignGenerator>> getGeneratorsMapPerID() {
        return generatorsPerIdentifier;
    }
}