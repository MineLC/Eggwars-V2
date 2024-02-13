package lc.eggwars.game.shopkeepers;

import io.netty.util.collection.IntObjectHashMap;

public final class ShopKeepersStorage {

    private static ShopKeepersStorage instance;

    private final String name;
    private final IntObjectHashMap<ShopkeepersData.Skin> skins;
    private final ShopkeepersData data;

    ShopKeepersStorage(String name, IntObjectHashMap<ShopkeepersData.Skin> skins, ShopkeepersData data) {
        this.name = name;
        this.skins = skins;
        this.data = data;
    }

    public ShopkeepersData.Skin getSkin(final int id) {
        return skins.get(id);
    }

    public ShopkeepersData getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    final static void update(ShopKeepersStorage data) {
        instance = data;
    }

    public static final ShopKeepersStorage getInstance() {
        return instance;
    }
}
