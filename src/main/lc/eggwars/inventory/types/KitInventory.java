package lc.eggwars.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.inventory.CustomInventory;

import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.Kit;
import obed.me.minecore.objects.Jugador;

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

        Jugador.getJugador(event.getWhoClicked().getName()).getServerStats().getStatsEggWars().setSelectedKit(clickedKit.name().hashCode());
        event.getWhoClicked().sendMessage(Messages.get("kit.selected").replace("%name%", clickedKit.name()));
    }

    public Inventory getInventory() {
        return inventory;
    }
}