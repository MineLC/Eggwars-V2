package lc.eggwars.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.eggwars.database.mongodb.MongoDBManager;
import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.others.tab.TabStorage;
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
        event.setQuitMessage(null);
        
        if (game != null) {
            GameStorage.getStorage().leave(game, player, true);
            TabStorage.getStorage().removeOnePlayer(player, game.getPlayers());
        } else if (SpawnStorage.getStorage().isInSpawn(player)) {
            TabStorage.getStorage().removeOnePlayer(player, SpawnStorage.getStorage().getPlayers());
        }

        CompletableFuture.runAsync(() -> {
            final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
            MongoDBManager.getManager().saveData(player.getUniqueId(), data);
        });
    }
}