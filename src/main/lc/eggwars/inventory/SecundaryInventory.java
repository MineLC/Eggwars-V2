package lc.eggwars.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface SecundaryInventory extends PrincipalInventory {
    public void handle(final InventoryClickEvent event);

    @Override
    default Inventory getInventory() {
        return null;
    }
}
