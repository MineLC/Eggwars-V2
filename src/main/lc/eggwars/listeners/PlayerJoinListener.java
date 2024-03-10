package lc.eggwars.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.tinylog.Logger;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import lc.eggwars.database.DatabaseManager;
import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;
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

        event.getPlayer().teleport(SpawnStorage.getStorage().location());
        SpawnStorage.getStorage().setItems(event.getPlayer());

        CompletableFuture.runAsync(() -> {
            try {
                final PlayerData data = DatabaseManager.getManager().getData(event.getPlayer().getUniqueId());
                PlayerDataStorage.getStorage().add(event.getPlayer().getUniqueId(), data);
                SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(event.getPlayer());            
            } catch (Exception e) {
                Logger.error(e);
            }
        });       
    }
}