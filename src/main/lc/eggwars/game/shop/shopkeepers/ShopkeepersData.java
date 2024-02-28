package lc.eggwars.game.shop.shopkeepers;

import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;

public final record ShopkeepersData(
    Inventory skinShopInventory,
    Inventory itemsShop,
    IntObjectHashMap<Skin> items
) {

    public final record Skin(String name, int id, String message, int addHeight, int cost) {
    }
}