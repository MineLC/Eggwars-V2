package lc.eggwars.listeners.others;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;

import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;

public final class BlockGrowListener implements EventListener {

    @ListenerData(
        event = BlockGrowEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        ((BlockGrowEvent)defaultEvent).setCancelled(true);
    }
}