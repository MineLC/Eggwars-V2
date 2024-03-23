package lc.eggwars.game.pregame;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
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
        inventory.clear();

        inventory.setItem(SpawnStorage.getStorage().shopItem().slot(), SpawnStorage.getStorage().shopItem().item());
        inventory.setItem(selectTeam.slot(), selectTeam.item());
        ((CraftInventory)inventory).getInventory().update();
    }

    static void update(PregameStorage newStorage) {
        storage = newStorage;
    }
}
