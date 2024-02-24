package lc.eggwars.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.inventory.CustomInventory;

import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.Kit;
import lc.eggwars.players.PlayerStorage;

public class KitInventory implements CustomInventory {

    private final IntObjectHashMap<Kit> inventoryItems;
    private final Inventory inventory;

    public KitInventory(IntObjectHashMap<Kit> kits, Inventory inventory) {
        this.inventoryItems = kits;
        this.inventory = inventory;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        event.setCancelled(true);
        final Kit clickedKit = inventoryItems.get(event.getSlot());

        if (clickedKit == null) {
            return;
        }

        PlayerStorage.getStorage().get(event.getWhoClicked().getUniqueId()).setKit(clickedKit);
        event.getWhoClicked().sendMessage(Messages.get("kit.selected").replace("%name%", clickedKit.name()));
    }

    public Inventory getInventory() {
        return inventory;
    }
}