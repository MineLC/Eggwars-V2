package lc.eggwars.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.tinylog.Logger;

import lc.eggwars.database.DatabaseManager;
import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());

        if (game != null) {
            GameStorage.getStorage().leave(game, player);
        }

        CompletableFuture.runAsync(() -> {
            try {
                final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
                DatabaseManager.getManager().saveData(player.getUniqueId(), data);
            } catch (Exception e) {
                Logger.error(e);
            }
        });
    }
}