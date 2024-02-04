package lc.eggwars.listeners;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.utils.ClickableBlock;

public final class PlayerInteractListener implements EventListener {

    @ListenerData(
        event = PlayerInteractEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerInteractEvent event = (PlayerInteractEvent)defaultEvent;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        final Location location = event.getClickedBlock().getLocation();
        final ClickableBlock block = MapStorage.getStorage().getClickableBlock(location.getWorld(), location);

        if (block != null) {
            block.onClick(event.getPlayer());
        }
    }
}