package lc.eggwars.game.shop.shopkeepers;

import io.netty.util.collection.IntObjectHashMap;

public final record ShopKeepersStorage(String customName, IntObjectHashMap<ShopkeepersData.Skin> skins, ShopkeepersData data) {

    private static ShopKeepersStorage storage;

    final static void update(ShopKeepersStorage newStorage) {
        storage = newStorage;
    }

    public static final ShopKeepersStorage getStorage() {
        return storage;
    }
}
