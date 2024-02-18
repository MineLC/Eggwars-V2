package lc.eggwars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.players.PlayerStorage;
import lc.eggwars.spawn.SpawnStorage;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameInProgress map = GameStorage.getStorage().getGame(player.getUniqueId());

        // TODO Before of delete player data, save in database

        PlayerStorage.getInstance().removePlayer(event.getPlayer().getUniqueId());
        player.getInventory().clear();

        if (map == null) {
            return;
        }

        player.teleport(SpawnStorage.getStorage().getLocation());
        GameStorage.getStorage().leave(map, player, map.getWorld());
    }
}