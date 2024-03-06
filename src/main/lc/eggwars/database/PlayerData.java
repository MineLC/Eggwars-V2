package lc.eggwars.database;

import gnu.trove.set.hash.TIntHashSet;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData;

public final class PlayerData {
    public int kills = 0,
                finalKills = 0,
                deaths = 0,
                finalDeaths = 0,
                wins = 0,
                skinSelected = 120,
                kitSelected = 0,
                destroyedEggs = 0,
                coins = 0,
                level = 0;

    public TIntHashSet skins; 
    public TIntHashSet kits; 

    public static PlayerData createEmptyData() {
        final PlayerData data = new PlayerData();
        data.skins = new TIntHashSet();
        data.skins.add(ShopkeepersData.VILLAGER_SKIN);
        data.kits = new TIntHashSet();

        return data;
    }
}