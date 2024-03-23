package lc.eggwars.others.specialitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import lc.eggwars.game.PlayerInGame;

public class JumpPadItem {

    public void handleUp(final Player player, final ItemStack item, final PlayerInGame data) {
        final Vector vector = player.getVelocity();
        final long time = System.currentTimeMillis();
        if (time - data.getJumpPadTime() < 5000) {
            player.sendMessage("Necesitas esperar " + (int)((time - data.getJumpPadTime()) / 1000) + " segundos más");
            return;
        }
        data.setJumpPadTime(time);
        player.setVelocity(new Vector(vector.getX(), 1, vector.getZ()));
        removeOneItem(player.getInventory(), item);
    }

    public void handleDirectional(final Player player, final ItemStack item, final PlayerInGame data) {
        final Vector vector = player.getLocation().getDirection().multiply(1.5D).setY(1.0D);
        final long time = System.currentTimeMillis();
        if (time - data.getJumpPadTime() < 5000) {
            player.sendMessage("Necesitas esperar " + (int)((time - data.getJumpPadTime()) / 1000) + " segundos más");
            return;
        }
        data.setJumpPadTime(time);
        player.setVelocity(vector);
        removeOneItem(player.getInventory(), item);
    }

    private void removeOneItem(final PlayerInventory inventory, final ItemStack item) {
        if (item.getAmount() == 1) {
            inventory.setItemInHand(null);
            return;
        }
        item.setAmount(item.getAmount() - 1);
        inventory.setItemInHand(item);
    }
}
