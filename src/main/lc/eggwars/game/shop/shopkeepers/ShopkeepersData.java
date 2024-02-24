package lc.eggwars.game.shop.shopkeepers;

import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;

public final record ShopkeepersData(
    Inventory inventory,
    IntObjectHashMap<Skin> items
) {

    public final record Skin(int id, String message, int addHeight) {
    }
}