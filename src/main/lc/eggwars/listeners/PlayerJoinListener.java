package lc.eggwars.listeners;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.database.mongodb.MongoDBManager;
import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;

public final class PlayerJoinListener implements EventListener {

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        final Player player = event.getPlayer();
        event.setJoinMessage(null);
        SpawnStorage.getStorage().sendToSpawn(event.getPlayer());
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }

        CompletableFuture.runAsync(() -> {
            final PlayerData data = MongoDBManager.getManager().getData(player.getUniqueId());
            PlayerDataStorage.getStorage().add(player.getUniqueId(), data);
            SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);            
        });       
    }
}