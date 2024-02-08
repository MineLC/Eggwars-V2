package lc.eggwars.generators.managers;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import lc.eggwars.generators.SignGenerator;
import lc.eggwars.mapsystem.GameMap;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.World;

public final class GeneratorsLoader {

    public void load(final GameMap map) {
        final SignGenerator[] generators = map.getGenerators();
        final World world = ((CraftWorld)map.getWorld()).getHandle();

        for (final SignGenerator generator : generators) {
            final Chunk mainChunk = world.getChunkAt(generator.getLocation().x() >> 4, generator.getLocation().z() >> 4);

            generator.getBase().dropItem().world = world;
            generator.updateChunks(getRadiusChunk(world, mainChunk));
        }
    }

    private Chunk[] getRadiusChunk(final World world, Chunk mainChunk) {
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