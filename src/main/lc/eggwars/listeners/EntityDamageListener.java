package lc.eggwars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.mapsystem.GameMap;

public final class EntityDamageListener implements EventListener {

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = EntityDamageEvent.class
    )
    public void handle(Event defaultEvent) {
        final EntityDamageEvent event = (EntityDamageEvent)defaultEvent;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());

        if (map == null) {
            event.setCancelled(true);
            event.setDamage(0);    
            return;
        }

        if (map.getState() == GameState.PREGAME) {           
            event.setCancelled(true);
            event.setDamage(0);

            if (event.getCause() == DamageCause.VOID) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
            return;
        }

        if (event.getCause() == DamageCause.VOID) {
            player.setHealth(0);
        }
    }
}