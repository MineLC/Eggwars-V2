package lc.eggwars.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.others.kits.KitStorage;

public final class SpawnShopInventory {

    private final Item skinShopItem;
    private final Item kitItem;
    private final Inventory inventory;

    public SpawnShopInventory(Item skinShopItem, Item kitItem, Inventory inventory) {
        this.skinShopItem = skinShopItem;
        this.kitItem = kitItem;
        this.inventory = inventory;
    }

    public void handle(InventoryClickEvent event) {
        final int slot = event.getSlot();

        if (slot == skinShopItem.slot()) {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(ShopKeepersStorage.getStorage().data().skinShopInventory());
            return;
        }
    
        if (slot == kitItem.slot()) {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(KitStorage.getStorage().inventory().getInventory());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}