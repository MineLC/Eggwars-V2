package lc.eggwars.listeners.inventory;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.inventory.CustomInventory;
import lc.eggwars.inventory.types.SkinShopInventory;
import lc.eggwars.others.kits.KitStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import lc.eggwars.spawn.SpawnStorage;
import lc.eggwars.utils.InventoryUtils;

public class PlayerInventoryClickListener implements EventListener {

    private final CustomInventory shopSkinInventory;

    public PlayerInventoryClickListener() {
        this.shopSkinInventory = new SkinShopInventory();
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

        if (!event.getWhoClicked().getWorld().equals(SpawnStorage.getStorage().location().getWorld())) {
            final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getWhoClicked().getUniqueId());

            if (gameInProgress == null || gameInProgress.getState() != GameState.PREGAME) {
                return;
            }
        }

        event.setCancelled(true);
            
        final int inventory = InventoryUtils.getId(event.getClickedInventory());

        if (inventory == -1) {
            return;
        }

        if (inventory == InventoryUtils.getId(SpawnStorage.getStorage().shopInventory().getInventory())) {
            SpawnStorage.getStorage().shopInventory().handle(event);
            return;
        }

        if (inventory == InventoryUtils.getId(ShopKeepersStorage.getStorage().data().skinShopInventory())) {
            shopSkinInventory.handle(event);
            return;
        }

        if (inventory == InventoryUtils.getId(KitStorage.getStorage().inventory().getInventory())) {
            KitStorage.getStorage().inventory().handle(event);
        }
    }
}