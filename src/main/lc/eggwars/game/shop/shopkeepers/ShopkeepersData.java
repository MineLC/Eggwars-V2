package lc.eggwars.game.shop.shopkeepers;

import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;

public final record ShopkeepersData(
    Inventory skinShopInventory,
    Inventory itemsShop,
    IntObjectHashMap<Skin> items
) {

    public static final int VILLAGER_SKIN = 120; // See EntitiesTypes

    public final record Skin(String name, int id, String message, int addHeight, int cost) {
    }
}