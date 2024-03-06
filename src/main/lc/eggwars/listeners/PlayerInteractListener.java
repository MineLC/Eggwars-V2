package lc.eggwars.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.utils.ClickableBlock;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerInteractListener implements EventListener {

    @ListenerData(
        event = PlayerInteractEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerInteractEvent event = (PlayerInteractEvent)defaultEvent;

        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            handleInteractWithItems(event);
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        final Location location = block.getLocation();
        final ClickableBlock clickableBlock = MapStorage.getStorage().getClickableBlock(location.getWorld(), location);
        
        if (clickableBlock != null) {
            clickableBlock.onClick(event.getPlayer(), event.getAction());
            event.setCancelled(true);
        }
    }

    private void handleInteractWithItems(final PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        final GameInProgress game = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());

        if (game != null) {
            if (game.getState() != GameState.PREGAME) {
                return;
            }

            return;
        }

        if (!event.getPlayer().getWorld().equals(SpawnStorage.getStorage().location().getWorld())) {
            return;
        }
        final Inventory inventory = SpawnStorage.getStorage().items().get(event.getItem().getType());
        if (inventory != null) {
            event.getPlayer().openInventory(inventory);
            event.setCancelled(true);
        }
    }
}