package lc.eggwars.listeners.pvp.damage;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.events.GameEventType;
import lc.eggwars.teams.GameTeam;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerDamageByPlayerListener implements EventListener {

    @ListenerData(
        priority = EventPriority.NORMAL,
        event = EntityDamageByEntityEvent.class
    )
    public void handle(final Event defaultEvent) {
        if (!(defaultEvent instanceof EntityDamageByEntityEvent event)) {
            return;
        }
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(event.getDamager().getUniqueId());
        if (game == null || game.getState() != GameState.IN_GAME
            || (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.TREASON)) {
            return;
        }

        final GameTeam playerTeam = game.getTeamPerPlayer().get(event.getDamager());

        if (playerTeam == null) {
            return;
        }
        final Set<Player> playersInTeam = playerTeam.getPlayers();

        if (playersInTeam.contains(event.getEntity())) {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        if (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.CRITICAL) {
            event.setDamage(event.getDamage() + (event.getDamage() / 100) * 25);
            return;
        }
        if (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.ONEDAMAGE) {
            event.setDamage(1);
        }
    }
}