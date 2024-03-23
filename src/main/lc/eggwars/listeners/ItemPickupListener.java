package lc.eggwars.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class ItemPickupListener implements EventListener {

    @ListenerData(
        event = PlayerPickupItemEvent.class,
        priority = EventPriority.NORMAL
    )
    public void handle(Event defaultEvent) {
        final PlayerPickupItemEvent event = (PlayerPickupItemEvent)defaultEvent;
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
    }
}