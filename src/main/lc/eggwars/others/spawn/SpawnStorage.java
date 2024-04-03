package lc.eggwars.others.spawn;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.inventory.types.SpawnShopInventory;

public final class SpawnStorage {

    private final Location location;
    private final Item shopItem, gameSelect;
    private final SpawnShopInventory shopInventory;
    private final Material shopItemMaterial, gameItemMaterial;

    SpawnStorage(Location location, Item shopItem, Item gameSelect, SpawnShopInventory shopInventory) {
        this.location = location;
        this.shopItem = shopItem;
        this.gameSelect = gameSelect;
        this.shopInventory = shopInventory;
        this.shopItemMaterial = shopItem.item().getType();
        this.gameItemMaterial = gameSelect.item().getType();
    }

    private static SpawnStorage storage;

    public void sendToSpawn(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setLevel(0);
        player.setFoodLevel(20);

        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(shopItem.slot(), shopItem.item());
        inventory.setItem(gameSelect.slot(), gameSelect.item());

        player.teleport(location);
    }

    public boolean isInSpawn(final HumanEntity player) {
        return (location == null)
            ? false
            : player.getLocation().getWorld().equals(location.getWorld());
    }

    public List<Player> getPlayers() {
        return location.getWorld().getPlayers();
    }

    public SpawnShopInventory getShopInventory() {
        return shopInventory;
    }

    public Item getShopItem() {
        return shopItem;
    }

    public Material getShopItemMaterial() {
        return shopItemMaterial;
    }

    public Material getGameItemMaterial() {
        return gameItemMaterial;
    }

    final static void update(SpawnStorage newStorage) {
        storage = newStorage;
    }

    final static void update(Location location) {
        storage = new SpawnStorage(location, storage.shopItem, storage.gameSelect, storage.shopInventory);
    }

    public static SpawnStorage getStorage() {
        return storage;
    }
}