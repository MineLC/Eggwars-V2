package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;
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

        // TODO Connect with database and get playerData
        PlayerStorage.getStorage().addPlayer(
            event.getPlayer().getUniqueId(),
            new PlayerData(VILLAGER_SKIN));
        
        SpawnStorage.getStorage().setItems(event.getPlayer());
    }
}