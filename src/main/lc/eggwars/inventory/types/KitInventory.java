package lc.eggwars.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;

import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.Kit;

public class KitInventory {

    private final IntObjectHashMap<Kit> inventoryItems;
    private final Inventory inventory;

    public KitInventory(IntObjectHashMap<Kit> kits, Inventory inventory) {
        this.inventoryItems = kits;
        this.inventory = inventory;
    }

    public void handle(InventoryClickEvent event) {
        event.setCancelled(true);
        final Kit clickedKit = inventoryItems.get(event.getSlot());

        if (clickedKit == null) {
            return;
        }
        final PlayerData data = PlayerDataStorage.getStorage().get(event.getWhoClicked().getUniqueId());

        if (data.kits.contains(clickedKit.id())) {
            data.kitSelected = clickedKit.id();
            event.getWhoClicked().sendMessage(Messages.get("kit.selected").replace("%name%", clickedKit.name()));
            return;
        }
        if (data.coins < clickedKit.cost()) {
            Messages.send(event.getWhoClicked(), "kit.no-money");
            return;
        }
        data.kits.add(clickedKit.id());
        data.kitSelected = clickedKit.id();
        data.coins -= clickedKit.cost();
        event.getWhoClicked().sendMessage(Messages.get("kit.selected").replace("%name%", clickedKit.name()));
    }

    public Inventory getInventory() {
        return inventory;
    }
}