package lc.eggwars.game.shop;

import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.shop.metadata.ItemMetaData;
import net.minecraft.server.v1_8_R3.ItemStack;

public final record Shop(Inventory inventory, IntObjectHashMap<Item> items) {

    public static final record Item(ItemStack buyItem, ItemStack needItem, int needAmount, boolean stackeable, ItemMetaData meta) {} 
}