package lc.eggwars.inventory.internal;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomInventoryHolder implements InventoryHolder {

    private final String id;
    private final int hash;

    public CustomInventoryHolder(String id) {
        this.id = id;
        this.hash = id.hashCode();
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof CustomInventoryHolder other) ? other.id.equals(this.id) : false;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}