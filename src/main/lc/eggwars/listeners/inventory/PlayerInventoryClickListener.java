package lc.eggwars.listeners.inventory;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.inventory.types.SkinShopInventory;
import lc.eggwars.inventory.types.TeamSelectorInventory;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.utils.InventoryUtils;

public class PlayerInventoryClickListener implements EventListener {

    private final SkinShopInventory shopSkinInventory;

    public PlayerInventoryClickListener(EggwarsPlugin plugin) {
        this.shopSkinInventory = new SkinShopInventory(plugin);
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

        final int inventory = InventoryUtils.getId(event.getClickedInventory());
        if (inventory == -1) {
            if (SpawnStorage.getStorage().isInSpawn(event.getWhoClicked())) {
                event.setCancelled(true);
                return;
            }
            final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getWhoClicked().getUniqueId());
            if (gameInProgress.getState() == GameState.PREGAME) {
                event.setCancelled(true);
            }
            return;
        }

        event.setCancelled(true);

        if (!SpawnStorage.getStorage().isInSpawn(event.getWhoClicked())) {
            final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getWhoClicked().getUniqueId());

            if (gameInProgress == null || gameInProgress.getState() != GameState.PREGAME) {
                return;
            }
            final PreGameTemporaryData temporaryData = ((PreGameCountdown)gameInProgress.getCountdown()).getTemporaryData();
            try {
                
                if (InventoryUtils.getId(temporaryData.getTeamSelectorInventory()) == inventory) {
                    new TeamSelectorInventory().handle(event, gameInProgress, temporaryData);
                    return;
                }   
            } catch (Exception e) {
                org.tinylog.Logger.error(e);
            }
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