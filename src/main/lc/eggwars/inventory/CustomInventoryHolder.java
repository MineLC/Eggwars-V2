package lc.eggwars.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomInventoryHolder implements InventoryHolder {

    private final int id;

    public CustomInventoryHolder(int id) {
        this.id = id;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof CustomInventoryHolder other) ? other.id == this.id : false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}