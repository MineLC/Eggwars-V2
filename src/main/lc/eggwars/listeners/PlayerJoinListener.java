package lc.eggwars.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import obed.me.minecore.database.servers.CoreEggwarsAPI;
import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;
import lc.eggwars.spawn.SpawnStorage;

public final class PlayerJoinListener implements EventListener {

    private static final int VILLAGER_SKIN = 120; // See EntityTypes ID

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        event.getPlayer().teleport(SpawnStorage.getStorage().location());

        CompletableFuture.runAsync( () -> {
            final Jugador jugador = Jugador.getJugador(event.getPlayer().getName());
            CoreEggwarsAPI.loadStats(jugador);
            final StatsEggWars statsEggWars = jugador.getServerStats().getStatsEggWars();

            if (statsEggWars.getShopKeeperSkinSelected() == 0) {
                statsEggWars.setShopKeeperSkinSelected(VILLAGER_SKIN);
            }
        });
        SpawnStorage.getStorage().setItems(event.getPlayer());
    }
}