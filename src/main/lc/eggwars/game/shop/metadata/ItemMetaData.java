package lc.eggwars.game.shop.metadata;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemMetaData {
    ItemStack parse(ItemStack item, Player player);
}