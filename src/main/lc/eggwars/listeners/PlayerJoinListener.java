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

import lc.eggwars.others.sidebar.EggwarsSidebar;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
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
            jugador.getServerStats().setStatsEggWars(new StatsEggWars());

            CoreEggwarsAPI.loadStats(jugador);
            if (jugador.getServerStats().getStatsEggWars() == null) {
                jugador.getServerStats().setStatsEggWars(createStats());
            }
            final EggwarsSidebar sidebar = SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN);
            sidebar.send(event.getPlayer());
        });
        SpawnStorage.getStorage().setItems(event.getPlayer());
    }

    private StatsEggWars createStats() {
        final StatsEggWars stats = new StatsEggWars();
        stats.setShopKeeperSkinSelected(VILLAGER_SKIN);
        stats.setKills(0);
        stats.setDeaths(0);
        stats.setLCoins(0);
        stats.setDestroyedEggs(0);
        stats.setLastDeath(0);
        stats.setLastKill(0);
        stats.setLevel(0);
        stats.setLoose(0);
        stats.setPlayed(0);
        stats.setWins(0);
        stats.setTeamDestroyed(0);
        stats.setKitList(new ArrayList<>());
        stats.setShopKeeperSkinList(new ArrayList<>());
        return stats;
    }
}