package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.spawn.SpawnStorage;

public final class PlayerJoinListener implements EventListener {

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        event.getPlayer().teleport(SpawnStorage.getStorage().getLocation());
    }
}