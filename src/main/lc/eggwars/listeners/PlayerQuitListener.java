package lc.eggwars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.eggwars.game.GameStorage;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.spawn.SpawnStorage;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());

        player.teleport(SpawnStorage.getStorage().getLocation());

        GameStorage.getStorage().leave(map, player, map.getWorld());
    }
}