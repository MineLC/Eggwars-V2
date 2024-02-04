package lc.eggwars.listeners.internal;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface EventListener extends Listener {
    void handle(Event defaultEvent);
}