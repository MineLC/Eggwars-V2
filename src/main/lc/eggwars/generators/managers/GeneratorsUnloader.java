package lc.eggwars.generators.managers;

import lc.eggwars.generators.SignGenerator;
import lc.eggwars.mapsystem.GameMap;

public final class GeneratorsUnloader {

    public void unload(final GameMap map) {
        final SignGenerator[] generators = map.getGenerators();

        for (final SignGenerator generator : generators) {
            generator.updateChunks(null);
            generator.getItem().world = null;
            generator.setAmount(0);
            generator.setDefaultLevel();
        }
    }
}