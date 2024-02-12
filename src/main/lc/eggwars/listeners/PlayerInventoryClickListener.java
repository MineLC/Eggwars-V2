package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.inventory.InventoryStorage;
import lc.eggwars.inventory.PrincipalInventory;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.spawn.SpawnStorage;
import lc.eggwars.utils.InventoryUtils;

public class PlayerInventoryClickListener implements EventListener {

    private final PrincipalInventory spawnShopInventory;
    private final PrincipalInventory shopSkinInventory;

    public PlayerInventoryClickListener() {
        this.spawnShopInventory = InventoryStorage.getStorage().getInventories().get("spawnshop");
        this.shopSkinInventory = InventoryStorage.getStorage().getInventories().get("skinshop");
    }

    @ListenerData(
        event = InventoryClickEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(final Event defaultEvent) {
        final InventoryClickEvent event = (InventoryClickEvent)defaultEvent;

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getWhoClicked().getWorld().equals(SpawnStorage.getStorage().getLocation().getWorld())) {
            event.setCancelled(true);
        }

        final int inventory = InventoryUtils.getId(event.getClickedInventory());

        if (inventory == -1) {
            return;
        }

        if (inventory == InventoryUtils.getId(spawnShopInventory.getInventory())) {
            spawnShopInventory.handle(event);
            return;
        }

        if (inventory == InventoryUtils.getId(ShopKeepersStorage.getInstance().getData().inventory())) {
            shopSkinInventory.handle(event);
            return;
        }
    }
}