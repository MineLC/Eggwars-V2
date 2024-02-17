package lc.eggwars.game.managers;

import org.bukkit.craftbukkit.CraftWorld;

import lc.eggwars.game.GameMap;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.GeneratorStorage;
import lc.eggwars.utils.BlockLocation;
import net.minecraft.server.Chunk;
import net.minecraft.server.World;

public final class GeneratorManager {

    public void setGeneratorSigns(final GameMap map) {
        final ClickableSignGenerator[] generators = map.getGenerators();

        for(final ClickableSignGenerator generator : generators) {
            final BlockLocation loc = generator.getLocation();
            GeneratorStorage.getStorage().setLines(
                map.getWorld().getBlockAt(loc.x(), loc.y(), loc.z()),
                generator.getBase(),
                generator.getDefaultLevel());
        }
    }

    public void load(final GameMap map) {
        final ClickableSignGenerator[] generators = map.getGenerators();
    
        for(final ClickableSignGenerator generator : generators) {
            generator.setGenerator(map.getWorld());
            final World world = ((CraftWorld)map.getWorld()).getHandle();

            generator.getGenerator().update(world, getNearbyChunk(
                world,
                world.getChunkAt(generator.getLocation().x() >> 4, generator.getLocation().z() >> 4)
            ));
        }
    }

    public void unload(final GameMap map) {
        final ClickableSignGenerator[] generators = map.getGenerators();
    
        for(final ClickableSignGenerator generator : generators) {
            generator.cleanData();
        }
    }

    private static final Chunk[] getNearbyChunk(final World world, Chunk mainChunk) {
        final Chunk[] nearbyChunks = new Chunk[9];
        nearbyChunks[0] = mainChunk;

        /*
         * # # #
         * # | #
         * # # #
         * 
         * Supongamos que '#' es un chunk cercano y '|' el chunk donde está el generador
         * Usa esta idea para entender el siguiente código:
         */

        nearbyChunks[1] = world.getChunkAt(mainChunk.locX + 1, mainChunk.locZ); // Arriba 
        nearbyChunks[2] = world.getChunkAt(mainChunk.locX, mainChunk.locZ + 1); // Lado derecho
        nearbyChunks[3] = world.getChunkAt(mainChunk.locX + 1, mainChunk.locZ + 1); // Extremo arriba derecho
        nearbyChunks[4] = world.getChunkAt(mainChunk.locX - 1, mainChunk.locZ); // Abajo
        nearbyChunks[5] = world.getChunkAt(mainChunk.locX, mainChunk.locZ - 1);  // Lado izquierdo
        nearbyChunks[6] = world.getChunkAt(mainChunk.locX - 1, mainChunk.locZ - 1); // Extremo abajo izquierdo
        nearbyChunks[7] = world.getChunkAt(mainChunk.locX -1, mainChunk.locZ + 1); // Extremo abajo derecho
        nearbyChunks[8] = world.getChunkAt(mainChunk.locX +1, mainChunk.locZ - 1); // Extremo arriba izquierdo
        return nearbyChunks;
    }
}