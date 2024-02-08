package lc.eggwars.generators.managers;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import lc.eggwars.generators.GeneratorStorage;
import lc.eggwars.generators.SignGenerator;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.utils.BlockLocation;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.World;

public final class GeneratorsLoader {

    public void load(final GameMap map) {
        final SignGenerator[] generators = map.getGenerators();
        final World world = ((CraftWorld)map.getWorld()).getHandle();

        for (final SignGenerator generator : generators) {
            final BlockLocation location = generator.getLocation();
            final Chunk mainChunk = world.getChunkAt(location.x() >> 4, location.z() >> 4);

            generator.getBase().dropItem().world = world;
            generator.updateChunks(getRadiusChunk(world, mainChunk));
        }
    }

    @Deprecated(forRemoval = true, since = "0.0.1 - Es lento y luego se va a usar nms")
    public void setGeneratorSigns(final GameMap map) {
        final SignGenerator[] generators = map.getGenerators();
        for (final SignGenerator generator : generators) {
            final BlockLocation location = generator.getLocation();
            GeneratorStorage.getStorage().setGeneratorLines(
                map.getWorld().getBlockAt(location.x(), location.y(), location.z()),
                generator);
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