package lc.eggwars.listeners;

import org.bukkit.event.Event;

import lc.lcspigot.events.PlayerJoinTabInfoEvent;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerJoinTabInfoListener implements EventListener {

    @ListenerData(event = PlayerJoinTabInfoEvent.class)
    public void handle(Event defaultEvent) {
        ((PlayerJoinTabInfoEvent)defaultEvent).setCancelled(true);
    }
}