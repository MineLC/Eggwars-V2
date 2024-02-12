package lc.eggwars.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.inventory.PrincipalInventory;
import lc.eggwars.inventory.InventoryCreator.Item;

public final class SpawnShopInventory implements PrincipalInventory {

    private final Inventory inventory;
    private final Item skinShopItem;

    public SpawnShopInventory(Item skinShopItem, Inventory inventory) {
        this.skinShopItem = skinShopItem;
        this.inventory = inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        final int slot = event.getSlot();

        if (slot == skinShopItem.slot()) {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(ShopKeepersStorage.getInstance().getData().inventory());
            return;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}