package lc.eggwars.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface CustomInventory {
    void handle(final InventoryClickEvent event);
}