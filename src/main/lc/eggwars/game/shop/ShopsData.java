package lc.eggwars.game.shop;

import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;

public record ShopsData(
    Inventory mainShop,
    IntObjectHashMap<Shop> shops,
    IntObjectHashMap<Inventory> shopsHeader
) {
    
}
