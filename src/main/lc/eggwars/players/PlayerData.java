package lc.eggwars.players;

public final class PlayerData {
    private int shopSkinID;

    public PlayerData(int shopId) {
        this.shopSkinID = shopId;
    }

    public int getShopSkinID() {
        return shopSkinID;
    }

    public void setShopSkinID(int id) {
        this.shopSkinID = id;
    }
}