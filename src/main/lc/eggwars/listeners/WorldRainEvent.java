package lc.eggwars.listeners;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldRainEvent implements EventListener {
    @ListenerData(
            event = WeatherChangeEvent.class,
            priority = EventPriority.LOWEST
    )
    @Override
    public void handle(Event event) {
        final WeatherChangeEvent e = (WeatherChangeEvent)event;
        e.setCancelled(true);
    }
}
