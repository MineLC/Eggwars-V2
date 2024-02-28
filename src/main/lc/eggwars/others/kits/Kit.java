package lc.eggwars.others.kits;

import org.bukkit.potion.PotionEffect;

import lc.eggwars.inventory.internal.InventoryCreator.Item;
import net.minecraft.server.v1_8_R3.ItemStack;

public final record Kit(
    int id,
    String name,
    Item inventoryItem,
    ItemStack[] armor,
    ItemStack[] items,
    PotionEffect[] potionEffects,
    int cost
) {
}