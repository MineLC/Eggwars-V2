package lc.eggwars.game.managers;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lc.eggwars.game.GameMap;
import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;
import lc.eggwars.utils.EntityLocation;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public final class ShopKeeperManager {

    public void send(final Collection<Player> players, final World world, final GameMap map) {
        for (final Player player : players) {
            send(player, PlayerStorage.getInstance().get(player.getUniqueId()), map);
        }
    }

    public void send(final Player player, final PlayerData data, final GameMap map) {
        for (final EntityLocation location : map.getShopSpawns()) {
            spawn(
                player,
                player.getWorld(),
                data.getShopSkinID(),
                location.x(),
                location.y() + ShopKeepersStorage.getInstance().getSkin(data.getShopSkinID()).addHeight(),
                location.z(),
                location.yaw());
        }
    }

    public int spawn(final Player player, final World world, final int entityId, int x, int y, int z, float yaw) {
        final Entity entity = createEntityById(entityId, ((CraftWorld)world).getHandle());

        if (!(entity instanceof EntityLiving livingEntity)) {
            return -1;
        }

        livingEntity.locX = x;
        livingEntity.locY = y;
        livingEntity.locZ = z;
        livingEntity.yaw = yaw;
    
        final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(livingEntity);
    
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
        return entity.getId();
    }

    public void deleteEntity(final int id, final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
    }

    private Entity createEntityById(int id, net.minecraft.server.v1_8_R3.World world) {  
        try {
            Class<? extends Entity> classEntity = EntityTypes.a(id);
            if (classEntity != null) {
                return (Entity)classEntity.getConstructor(World.class).newInstance(world);
            }
        } catch (Exception var4) {
           var4.printStackTrace();
        }
  
        return null;
     }
}