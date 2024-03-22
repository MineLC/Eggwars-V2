package lc.eggwars.game.threadtasks;

import java.util.List;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.TemporaryGenerator;
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

public final class GeneratorTask {

    private final MapData[] maps;

    public GeneratorTask(MapData[] maps) {
        this.maps = maps;
    }

    public void execute() {
        for (final MapData map : maps) {
            if (map != null && map.getGameInProgress() != null) {
                generateItems(map.getGameInProgress());
            }
        }
    }

    private void generateItems(final GameInProgress game) {
        if (game.getState() != GameState.IN_GAME) {
            return;
        }

        final ClickableSignGenerator[] generators = game.getMapData().getGenerators();

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
        
            generator.addItemsToGenerate();
        
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
        final ClickableSignGenerator data = generator.getData();
        final Chunk[] chunks = generator.getChunks();

        for (final Chunk nearbyChunk : chunks) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                final int playerX = (int)entityPlayer.locX;
                final int playerY = (int)entityPlayer.locY;
                final int playerZ = (int)entityPlayer.locZ;
                if (isNearby(data.getMinLocation(), data.getMaxLocation(), playerX, playerY, playerZ)) {
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
        item.d(generator.hashCode());

        final PacketPlayOutSpawnEntity packetEntity = new PacketPlayOutSpawnEntity(item, 2, item.getId());
        final PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(item.getId(), item.getDataWatcher(), true);
        final ClickableSignGenerator data = generator.getData();
        final Chunk[] chunks = generator.getChunks();

        for (final Chunk nearbyChunk : chunks) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                final int playerX = (int)entityPlayer.locX;
                final int playerY = (int)entityPlayer.locY;
                final int playerZ = (int)entityPlayer.locZ;
                if (isNearby(data.getMinLocation(), data.getMaxLocation(), playerX, playerY, playerZ)) {
                    pickupItem(generator, entityPlayer);
                    destroyItem(item.getId(), generator);
                    break;
                }
                entityPlayer.playerConnection.networkManager.handle(packetEntity);
                entityPlayer.playerConnection.networkManager.handle(meta);
            }
            continue;
        }
    }

    private void pickupItem(final TemporaryGenerator generator, final EntityPlayer player) {
        generator.setAmount(InventoryUtils.addItem(generator.getItem(), generator.getAmount(), player.inventory));

        final PacketPlayOutNamedSoundEffect itemPickupSound = new PacketPlayOutNamedSoundEffect("random.pop", player.locX, player.locY, player.locZ, 1.0f, 1.0f);
        player.playerConnection.networkManager.handle(itemPickupSound);
    }

    private void destroyItem(final int itemId, final TemporaryGenerator generator) {
        final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(itemId);
        final Chunk[] chunks = generator.getChunks();
        for (final Chunk nearbyChunk : chunks) {
            final List<Entity> entities = generator.getEntities(nearbyChunk);

            for (final Entity entity : entities) {
                if (!(entity instanceof EntityPlayer entityPlayer)) {
                    continue;
                }
                entityPlayer.playerConnection.networkManager.handle(destroy);
            }
        }
    }
   
    private static final boolean isNearby(final BlockLocation min, final BlockLocation max, final int x, final int y, final int z) {
        return
            x >= min.x() &&
            x <= max.x() &&
            z >= min.z() &&
            z <= max.z() &&
            y >= min.y() &&
            y <= max.y();
    }
}