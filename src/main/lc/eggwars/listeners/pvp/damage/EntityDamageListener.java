package lc.eggwars.listeners.pvp.damage;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.pregame.PregameStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

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
        if (SpawnStorage.getStorage().isInSpawn(player)) {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());

        if (game == null) {
            return;
        }

        if (game.getState() == GameState.PREGAME) {           
            event.setCancelled(true);
            event.setDamage(0);

            if (event.getCause() == DamageCause.FIRE) {
                event.getEntity().setFireTicks(0);
            }

            if (event.getCause() == DamageCause.VOID) {
                player.teleport(PregameStorage.getStorage().mapLocation());
            }
            return;
        }

        if (game.getState() == GameState.END_GAME) {
            event.setCancelled(true);
            if (event.getCause() == DamageCause.VOID) {
                player.teleport(game.getWorld().getSpawnLocation());
            }
            return;
        }

        if (event.getCause() == DamageCause.VOID) {
            player.setHealth(0);
            event.setCancelled(true);
        }
    }
}