package lc.eggwars.game.generators;

import java.util.List;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.InventoryUtils;

import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;

public class GeneratorThread extends Thread {

    private static GeneratorThread currentThread;

    private final MapData[] maps;
    private boolean run = true;

    public GeneratorThread(MapData[] maps) {
        this.maps = maps;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(1000);
                for (final MapData map : maps) {
                    if (map != null && map.getGameInProgress() != null) {
                        generateItems(map.getGameInProgress());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateItems(final GameInProgress map) {
        if (map.getState() != GameState.IN_GAME) {
            return;
        }

        final ClickableSignGenerator[] generators = map.getMapData().getGenerators();

        for (final ClickableSignGenerator signGenerator : generators) {
            final TemporaryGenerator generator = signGenerator.getGenerator();
            if (generator.getBase().levels()[generator.getLevel()].itemsToGenerate() == 0) {
                continue;
            }

            generator.addOneSecond();

            if (generator.getWaitToSpawn() != generator.getWaitedTime()) {
                if (generator.getAmount() == 0 || generator.getEntitiesInNearbyChunks() == 0) {
                    continue;
                }
                if (generator.canRefreshItem()) {
                    tryPickupItemAndRegenerate(generator);
                    continue;
                }
                tryPickupItem(generator);
                continue;
            }
        
            generator.addItem();
        
            if (generator.getAmount() >= 64) {
                generator.setAmount(64);
            }
        
            generator.resetWaitedTime();
        
            if (generator.getEntitiesInNearbyChunks() != 0) {
                tryPickupItemAndRegenerate(generator);
            }
        }
    }

    private void tryPickupItem(final TemporaryGenerator generator) {
        for (final Chunk nearbyChunk : generator.getChunks()) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                final BlockLocation loc = generator.loc();
                if (isNearby(loc.x(), loc.y(), loc.z(), entityPlayer)) {
                    pickupItem(generator, entityPlayer);
                    destroyItem(generator.getEntityItem().getId(), generator);
                    break;
                }
            }
            continue;
        }
    }

    private void tryPickupItemAndRegenerate(final TemporaryGenerator generator) {
        final Entity item = generator.getEntityItem();
        item.setCustomName(String.valueOf(generator.getAmount()));

        final PacketPlayOutSpawnEntity packetEntity = new PacketPlayOutSpawnEntity(item, 2, item.getId());
        final PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(item.getId(), item.getDataWatcher(), true);

        for (final Chunk nearbyChunk : generator.getChunks()) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                final BlockLocation loc = generator.loc();
                if (isNearby(loc.x(), loc.y(), loc.z(), entityPlayer)) {
                    pickupItem(generator, entityPlayer);
                    destroyItem(item.getId(), generator);
                    break;
                }
                entityPlayer.playerConnection.sendPacket(packetEntity);
                entityPlayer.playerConnection.sendPacket(meta);
            }
            continue;
        }
    }

    private void pickupItem(final TemporaryGenerator generator, final EntityPlayer player) {
        generator.setAmount(InventoryUtils.addItem(generator.getItem(), generator.getAmount(), player.inventory));

        final PacketPlayOutNamedSoundEffect itemPickupSound = new PacketPlayOutNamedSoundEffect("random.pop", player.locX, player.locY, player.locZ, 1.0f, 1.0f);
        player.playerConnection.sendPacket(itemPickupSound);
    }

    private void destroyItem(final int itemId, final TemporaryGenerator generator) {
        final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(itemId);

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
            Math.max(x, player.locX) - Math.min(x, player.locX) <= 1.5 &&
            Math.max(y, player.locY) - Math.min(y, player.locY) <= 1.5 &&
            Math.max(z, player.locZ) - Math.min(z, player.locZ) <= 1.5;
    }

    public static void setThread(final GeneratorThread thread) {
        currentThread = thread;
    }

    public static void stopThread() {
        currentThread.run = false;
    }
}