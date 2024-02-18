package lc.eggwars.game.managers;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;
import lc.eggwars.utils.EntityLocation;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public final class ShopKeeperManager {

    public void send(final Collection<Player> players, final World world, final GameInProgress game) {
        for (final Player player : players) {
            send(player, PlayerStorage.getInstance().get(player.getUniqueId()), game);
        }
    }

    public void send(final Player player, final PlayerData data, final GameInProgress game) {
        int index = 0;
        for (final EntityLocation location : game.getMapData().getShopSpawns()) {
            spawn(
                player,
                player.getWorld(),
                data.getShopSkinID(),
                game.getMapData().getShopIDs()[index++],
                location.x(),
                location.y() + ShopKeepersStorage.getInstance().getSkin(data.getShopSkinID()).addHeight(),
                location.z(),
                location.yaw());
        }
    }

    public int spawn(final Player player, final World world, final int typeID, final int entityID, int x, int y, int z, float yaw) {
        final Entity entity = createEntityById(typeID, ((CraftWorld)world).getHandle());

        if (!(entity instanceof EntityLiving livingEntity)) {
            return -1;
        }
        entity.d(entityID);
        entity.setCustomName(ShopKeepersStorage.getInstance().getName());
        entity.setCustomNameVisible(true);

        livingEntity.locX = x + 0.5D;
        livingEntity.locY = y;
        livingEntity.locZ = z + 0.5D;
        livingEntity.yaw = yaw;
    
        final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(livingEntity);
        final PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(livingEntity.getId(), livingEntity.getDataWatcher(), true);
    
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(data);
        return entity.getId();
    }

    public void deleteEntity(final int id, final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
    }

    private Entity createEntityById(int typeID, net.minecraft.server.v1_8_R3.World world) {  
        try {
            Class<? extends Entity> classEntity = EntityTypes.a(typeID);

            if (classEntity != null) {
                return (Entity)classEntity.getConstructor(net.minecraft.server.v1_8_R3.World.class).newInstance(world);
            }
        } catch (Exception var4) {
           var4.printStackTrace();
        }
  
        return null;
     }
}