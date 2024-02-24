package lc.eggwars.listeners.pvp;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerDeathListener implements EventListener {

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerDeathEvent.class
    )
    public void handle(Event defaultEvent) {
        if (!(defaultEvent instanceof PlayerDeathEvent)) {
            return;
        }
        final PlayerDeathEvent event = ((PlayerDeathEvent)defaultEvent);
        event.getEntity().spigot().respawn();
        event.setDeathMessage(null);
    }
}