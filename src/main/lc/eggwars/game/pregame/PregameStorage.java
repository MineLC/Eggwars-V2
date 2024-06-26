package lc.eggwars.game.pregame;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.others.spawn.SpawnStorage;

public record PregameStorage(Location mapLocation, boolean addShopSpawnitem, Item selectTeam) {
    private static PregameStorage storage;

    public static PregameStorage getStorage() {
        return storage;
    }

    public void send(final Player player) {
        final PlayerInventory inventory = player.getInventory();
        final Item shopItem = SpawnStorage.getStorage().getShopItem();
        inventory.clear();
        inventory.setItem(selectTeam.slot(), selectTeam.item());
        inventory.setItem(shopItem.slot(), shopItem.item());
    }

    static void update(PregameStorage newStorage) {
        storage = newStorage;
    }
}
