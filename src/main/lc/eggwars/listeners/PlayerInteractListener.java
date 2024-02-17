package lc.eggwars.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import lc.eggwars.inventory.PrincipalInventory;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.spawn.SpawnStorage;
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

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
            && event.getPlayer().getWorld().equals(SpawnStorage.getStorage().getLocation().getWorld())
        ) {
            event.setCancelled(true);
            if (event.getItem() == null) {
                return;
            }
            final PrincipalInventory inventory = SpawnStorage.getStorage().getItems().get(event.getItem().getType());
            if (inventory != null) {
                event.getPlayer().openInventory(inventory.getInventory());
            }
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
}