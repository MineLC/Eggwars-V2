package lc.eggwars.game.managers;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import gnu.trove.iterator.TIntIterator;
import lc.eggwars.database.PlayerDataStorage;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shop.shopkeepers.ShopkeeperEntity;
import lc.eggwars.utils.EntityLocation;


import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

public final class ShopKeeperManager {

    public void send(final Collection<Player> players, final GameInProgress game) {
        for (final Player player : players) {
            send(player, game);
        }
    }

    public void send(final Player player, final GameInProgress game) {
        final TIntIterator shopsID = game.getMapData().getShopsID().iterator();
        final int shopID = PlayerDataStorage.getStorage().get(player.getUniqueId()).skinSelected;

        for (final EntityLocation location : game.getMapData().getShopSpawns()) {
            spawn(player, player.getWorld(), shopID, shopsID.next(), location);
        }
    }

    public int spawn(final Player player, final World world, final int typeID, final int entityID, final EntityLocation location) {
        final EntityLiving entity = new ShopkeeperEntity(((CraftWorld)world).getHandle());
        entity.d(entityID);
        entity.setCustomName(ShopKeepersStorage.getStorage().customName());
        entity.setCustomNameVisible(true);

        entity.locX = location.x() + 0.5D;
        entity.locY = location.y() + ShopKeepersStorage.getStorage().skins().get(typeID).addHeight();
        entity.locZ = location.z() + 0.5D;
        entity.yaw = location.yaw();
        entity.pitch = location.pitch();
    
        final PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(entity, typeID);
        final PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
    
        ((CraftPlayer)player).getHandle().playerConnection.networkManager.handle(spawn);
        ((CraftPlayer)player).getHandle().playerConnection.networkManager.handle(data);
        return entity.getId();
    }

    public void deleteEntity(final int id, final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.networkManager.handle(new PacketPlayOutEntityDestroy(id));
    }
}