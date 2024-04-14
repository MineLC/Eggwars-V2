package lc.eggwars.game.managers;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.GeneratorStorage;
import lc.eggwars.utils.BlockLocation;

public final class GeneratorManager {

    public void setGeneratorSigns(final GameInProgress game) {
        final ClickableSignGenerator[] generators = game.getMapData().getGenerators();

        for(final ClickableSignGenerator generator : generators) {
            final BlockLocation loc = generator.getLocation();
            GeneratorStorage.getStorage().setLines(
                game.getWorld().getBlockAt(loc.x(), loc.y(), loc.z()),
                generator.getBase(),
                generator.getDefaultLevel());
        }
    }

    public void load(final GameInProgress game) {
        final ClickableSignGenerator[] generators = game.getMapData().getGenerators();
        int id = Integer.MAX_VALUE - game.getMapData().getShopSpawns().length;

        for(final ClickableSignGenerator generator : generators) {
            generator.setGenerator(game.getWorld(), --id);
        }
    }

    public void unload(final GameInProgress game) {
        final ClickableSignGenerator[] generators = game.getMapData().getGenerators();
    
        for(final ClickableSignGenerator generator : generators) {
            generator.cleanData();
        }
    }
}