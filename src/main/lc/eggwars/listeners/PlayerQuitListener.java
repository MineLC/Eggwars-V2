package lc.eggwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.eggwars.game.GameStorage;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.mapsystem.MapStorage;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null) {
            return;
        }
        GameStorage.getStorage().remove(player.getUniqueId());
        map.getPlayers().remove(player);
        map.getPlayersPerTeam().remove(player);

        if (map.getPlayers().size() == 0) {
            if (map.getTaskId() != -1) {
                Bukkit.getScheduler().cancelTask(map.getTaskId());
            }

            GameStorage.getStorage().unloadGame(map);
            MapStorage.getStorage().unload(player.getWorld());
        }
    }
}