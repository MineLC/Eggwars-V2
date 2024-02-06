package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.eggwars.game.GameStorage;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.mapsystem.GameMap;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final GameMap map = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());
        if (map == null) {
            return;
        }
        map.getPlayers().remove(event.getPlayer());
        map.getPlayersPerTeam().remove(event.getPlayer());
    }
}