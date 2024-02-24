package lc.eggwars.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.spawn.SpawnStorage;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import obed.me.minecore.database.servers.CoreEggwarsAPI;
import obed.me.minecore.objects.Jugador;

public final class PlayerQuitListener implements EventListener {

    @ListenerData(
        event = PlayerQuitEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerQuitEvent event = (PlayerQuitEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameInProgress map = GameStorage.getStorage().getGame(player.getUniqueId());

        CompletableFuture.runAsync( () -> CoreEggwarsAPI.saveStats(Jugador.getJugador(event.getPlayer().getName())));
        player.getInventory().clear();

        if (map == null) {
            return;
        }

        player.teleport(SpawnStorage.getStorage().location());
        GameStorage.getStorage().leave(map, player, map.getWorld());
    }
}