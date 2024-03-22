package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerDropitemListener implements EventListener {

    @ListenerData(
        event = PlayerDropItemEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerDropItemEvent event = (PlayerDropItemEvent)defaultEvent;
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());
        if (gameInProgress != null && gameInProgress.getState() == GameState.PREGAME) {
            event.setCancelled(true);
        }
    }
}
