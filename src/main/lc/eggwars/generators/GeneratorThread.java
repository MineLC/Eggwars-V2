package lc.eggwars.generators;

import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import lc.eggwars.game.GameState;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ItemUtils;

import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.World;

public final class GeneratorThread extends Thread {

    private static GeneratorThread generatorThread;

    private final GameMap[] maps;
    private boolean run = true;

    public GeneratorThread(GameMap[] maps) {
        this.maps = maps;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(1000);
                for (final GameMap map : maps) {
                    if (map.getState() != GameState.IN_GAME) {
                        continue;
                    }
                    if (!tryGenerate(map.getGenerators(), ((CraftWorld)map.getWorld()).getHandle())) {
                        continue;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }        
    }

    private boolean tryGenerate(final SignGenerator[] generators, final World world) {
        for (final SignGenerator generator : generators) {
            if (canGenerateItem(generator)) {
                return false;
            }

            if (generator.getAmount() >= 256) {
                continue;
            }

            int amountToGenerate = generator.getAmount() + generator.getBase().levels()[generator.getLevel()].amountGenerated();

            if (amountToGenerate > 256) {
                amountToGenerate = 256;
            }

            generateItem(world, generator, amountToGenerate);
        }
        return true;
    }

    private boolean canGenerateItem(final SignGenerator generator) {
        int amountEntities = 0;
        for (final Chunk chunk : generator.getChunks()) {
            final List<Entity> entities = generator.getEntities(chunk);
            if (entities != null) {
                amountEntities += entities.size();
            }
        }

        if (amountEntities == 0) {
            return false;
        }

        return true;
    }

    private void generateItem(final World world, final SignGenerator generator, final int amountToGenerate) {
        final EntityItem item = generator.getBase().dropItem();

        item.world = world;
        item.locX = generator.getLocation().x();
        item.locY = generator.getLocation().y();
        item.locZ = generator.getLocation().z();

        item.setCustomName(String.valueOf(generator.getAmount()));

        final PacketPlayOutSpawnEntity packetEntity = new PacketPlayOutSpawnEntity(item, 2, item.getId());
        final PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(item.getId(), item.getDataWatcher(), true);

        boolean pickupItem = false;

        chunkLoop : for (final Chunk nearbyChunk : generator.getChunks()) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                final BlockLocation location = generator.getLocation();
                if (isNearby(location.x(), location.y(), location.z(), entityPlayer)) {
                    generator.setAmount(ItemUtils.addItem(generator.getBase().item(), amountToGenerate, entityPlayer.inventory));

                    final PacketPlayOutNamedSoundEffect itemPickupSound = new PacketPlayOutNamedSoundEffect("random.pop", entityPlayer.locX, entityPlayer.locY, entity.locZ, 1.0f, 1.0f);
                    entityPlayer.playerConnection.sendPacket(itemPickupSound);
                    pickupItem = true;
                    break chunkLoop;
                }
                entityPlayer.playerConnection.sendPacket(packetEntity);
                entityPlayer.playerConnection.sendPacket(meta);
            }
            continue;
        }

        if (!pickupItem) {
            return;
        }

        final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(item.getId());

        for (final Chunk nearbyChunk : generator.getChunks()) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                entityPlayer.playerConnection.sendPacket(destroy);
            }
        }
    }


    private static final boolean isNearby(final int x, final int y, final int z, final EntityPlayer player) {
        return
            Math.max(x, player.locX) - Math.min(x, player.locX) <= 2.5D &&
            Math.max(y, player.locY) - Math.min(y, player.locY) <= 2.5D &&
            Math.max(z, player.locZ) - Math.min(z, player.locZ) <= 2.5D;
    }

    public void stopThread() {
        this.run = false;
    }

    public static void setThread(final GeneratorThread thread) {
        if (generatorThread != null) {
            generatorThread.stopThread();
        }
        generatorThread = thread;
    }
}