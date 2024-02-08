package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPhysicsEvent;

import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;

public final class BlockPhysicsListener implements EventListener {

    @ListenerData(
        event = BlockPhysicsEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        ((BlockPhysicsEvent)defaultEvent).setCancelled(true);
    }
}