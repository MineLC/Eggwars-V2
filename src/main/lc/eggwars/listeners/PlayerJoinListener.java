package lc.eggwars.listeners;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import obed.me.minecore.database.servers.CoreEggwarsAPI;
import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;

public final class PlayerJoinListener implements EventListener {

    private static final int VILLAGER_SKIN = 120; // See EntityTypes ID

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        event.getPlayer().teleport(SpawnStorage.getStorage().location());

        SpawnStorage.getStorage().setItems(event.getPlayer());

        CompletableFuture.runAsync(() -> {
            final Jugador jugador = Jugador.getJugador(event.getPlayer().getName());
            jugador.getServerStats().setStatsEggWars(new StatsEggWars());

            CoreEggwarsAPI.loadStats(jugador);
            tryCreateStats(jugador.getServerStats().getStatsEggWars());
            SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(event.getPlayer());
        });
    }

    private void tryCreateStats(StatsEggWars stats) {
        if (stats.getSelectedKit() == 0) {
            stats.setSelectedKit(VILLAGER_SKIN);
        }
        if (stats.getShopKeeperSkinList().isEmpty()) {
            final ArrayList<String> skins = new ArrayList<>();
            skins.add(String.valueOf(VILLAGER_SKIN));
            stats.setShopKeeperSkinList(skins);   
        }
    }
}