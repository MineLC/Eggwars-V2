package lc.eggwars.others.specialitems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lc.eggwars.messages.Messages;

public final class PlatformItem {

    public void handle(final Player player, final ItemStack item) {
        final Location location = player.getLocation();
        final int y = location.getBlockY() - 6;

        if (y < 10 || y > 150) {
            Messages.send(player, "special-items.platform-cant-use");
            return;
        }
        final int x = location.getBlockX();
        final int z = location.getBlockZ();
        final World world = player.getWorld();

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 100));

        setBlock(world, x, y, z);

        setBlock(world, x - 1, y, z);
        setBlock(world, x + 1, y, z);

        setBlock(world, x, y, z - 1);
        setBlock(world, x, y, z + 1);

        setBlock(world, x -1 , y, z - 1);
        setBlock(world, x + 1, y, z - 1);
        setBlock(world, x - 1, y, z + 1);
        setBlock(world, x + 1, y, z + 1);

        if (item.getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInHand(item);
    }

    private void setBlock(final World world, final int x, final int y, final int z) {
        final Block block = world.getBlockAt(x, y, z);
        if (block.getType() == Material.AIR) {
            block.setType(Material.GLASS);
        }
    }
}