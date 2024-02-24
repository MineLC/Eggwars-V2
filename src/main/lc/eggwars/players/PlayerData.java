package lc.eggwars.players;

import lc.eggwars.others.kits.Kit;

public final class PlayerData {
    private int shopSkinID;
    private Kit selectedKit;

    public PlayerData(int shopId) {
        this.shopSkinID = shopId;
    }

    public int getShopSkinID() {
        return shopSkinID;
    }

    public Kit getKit() {
        return selectedKit;
    }

    public void setShopSkinID(int id) {
        this.shopSkinID = id;
    }

    public void setKit(Kit kit) {
        this.selectedKit = kit;
    }
}