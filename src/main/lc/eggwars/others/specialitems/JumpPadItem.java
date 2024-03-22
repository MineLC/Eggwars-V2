package lc.eggwars.others.specialitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class JumpPadItem {

    public void handleUp(final Player player, final ItemStack item) {
        final Vector vector = player.getVelocity();
        player.setVelocity(new Vector(vector.getX(), vector.getY() + 5, vector.getZ()));
            
        if (item.getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInHand(item);
    }

    public void handleDirectional(final Player player, final ItemStack item) {
        final Vector vector = player.getLocation().getDirection().multiply(3.0D).setY(1.0D);
        player.setVelocity(vector);
     
        if (item.getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInHand(item);
    }
}
