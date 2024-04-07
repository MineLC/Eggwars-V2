package lc.eggwars.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import lc.eggwars.others.selectgame.MapInventoryBuilder;

public final class SelectMapInventory {
    private final Inventory inventory;
    private final int soloSlot, teamSlot;
    private final MapInventoryBuilder builder;

    public SelectMapInventory(MapInventoryBuilder builder, Inventory inventory, int soloSlot, int teamSlot) {
        this.builder = builder;
        this.inventory = inventory;
        this.soloSlot = soloSlot;
        this.teamSlot = teamSlot;
    }

    public void handle(final InventoryClickEvent event) {
        if (event.getSlot() == soloSlot) {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(builder.build(true));
            return;
        }
        if (event.getSlot() == teamSlot) {
            event.setCancelled(true);
            event.getWhoClicked().openInventory(builder.build(false));
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}