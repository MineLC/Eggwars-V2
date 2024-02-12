package lc.eggwars.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
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
        event.getPlayer().teleport(SpawnStorage.getStorage().getLocation());

        // TODO Connect with database and get playerData
        PlayerStorage.getInstance().addPlayer(
            event.getPlayer().getUniqueId(),
            new PlayerData(VILLAGER_SKIN));
    }
}