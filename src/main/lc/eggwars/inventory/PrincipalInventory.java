package lc.eggwars.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface PrincipalInventory {
    void handle(final InventoryClickEvent event);
    Inventory getInventory();
}