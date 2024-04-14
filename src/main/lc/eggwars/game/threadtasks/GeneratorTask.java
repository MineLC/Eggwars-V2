package lc.eggwars.game.threadtasks;

import java.util.Set;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.TemporaryGenerator;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.InventoryUtils;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;

public final class GeneratorTask {

    public void execute(final MapData map) {
        generateItems(map.getGameInProgress());
    }

    private void generateItems(final GameInProgress game) {
        if (game.getState() != GameState.IN_GAME) {
            return;
        }

        final ClickableSignGenerator[] generators = game.getMapData().getGenerators();

        for (final ClickableSignGenerator signGenerator : generators) {
            final TemporaryGenerator generator = signGenerator.getGenerator();
            if (generator == null) {
                continue;
            }
            if (generator.getBase().levels()[generator.getLevel()].itemsToGenerate() == 0) {
                continue;
            }

            generator.addOneSecond();
            final Set<Player> players = game.getPlayers();
            if (generator.getWaitToSpawn() != generator.getWaitedTime()) {
                if (generator.getAmount() == 0) {
                    continue;
                }
                if (generator.canRefreshItem()) {
                    tryPickupItemAndRegenerate(generator, players);
                    continue;
                }
                tryPickupItem(generator, players);
                continue;
            }
        
            generator.addItemsToGenerate();
        
            if (generator.getAmount() >= 64) {
                generator.setAmount(64);
            }
        
            generator.resetWaitedTime();
            tryPickupItemAndRegenerate(generator, players);
        }
    }

    private void tryPickupItem(final TemporaryGenerator generator, final Set<Player> players) {
        final ClickableSignGenerator data = generator.getData();

        for (final Player bukkitPlayer : players) {
            final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
            final int playerX = (int)player.locX;
            final int playerY = (int)player.locY;
            final int playerZ = (int)player.locZ;

            if (isEnter(data.getMinView(), data.getMaxPickup(), playerX, playerY, playerZ)) {
                pickupItem(generator, player);
                destroyItem(generator, player);
                break;
            }
            continue;
        }
    }

    private void tryPickupItemAndRegenerate(final TemporaryGenerator generator, final Set<Player> players) {
        final Entity item = generator.getEntityItem();
        item.setCustomName(String.valueOf(generator.getAmount()));
        item.d(generator.hashCode());
        
        final PacketPlayOutSpawnEntity packetEntity = new PacketPlayOutSpawnEntity(item, 2, item.getId());
        final PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(item.getId(), item.getDataWatcher(), true);
    
        final ClickableSignGenerator data = generator.getData();

        for (final Player bukkitPlayer : players) {
            final EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
            final int playerX = (int)player.locX;
            final int playerY = (int)player.locY;
            final int playerZ = (int)player.locZ;

            if (isEnter(data.getMinView(), data.getMaxView(), playerX, playerY, playerZ)) {
                if (isEnter(data.getMinPickup(), data.getMaxPickup(), playerX, playerY, playerZ)) {
                    pickupItem(generator, player);
                    destroyItem(generator, player);
                    break;
                }
                player.playerConnection.networkManager.handle(packetEntity);
                player.playerConnection.networkManager.handle(meta);
                break;
            }
            continue;
        }
    }

    private void pickupItem(final TemporaryGenerator generator, final EntityPlayer player) {
        generator.setAmount(InventoryUtils.addItem(generator.getItem(), generator.getAmount(), player.inventory));

        final PacketPlayOutNamedSoundEffect itemPickupSound = new PacketPlayOutNamedSoundEffect("random.pop", player.locX, player.locY, player.locZ, 1.0f, 1.0f);
        player.playerConnection.networkManager.handle(itemPickupSound);
    }

    private void destroyItem(final TemporaryGenerator generator, final EntityPlayer player) {
        final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(generator.hashCode());
        player.playerConnection.networkManager.handle(destroy);
    }
   
    private static final boolean isEnter(final BlockLocation min, final BlockLocation max, final int x, final int y, final int z) {
        return
            x >= min.x() &&
            x <= max.x() &&
            z >= min.z() &&
            z <= max.z() &&
            y >= min.y() &&
            y <= max.y();
    }
}