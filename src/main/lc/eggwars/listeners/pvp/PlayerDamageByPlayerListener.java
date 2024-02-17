package lc.eggwars.listeners.pvp;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import lc.eggwars.game.GameMap;
import lc.eggwars.game.GameStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.teams.BaseTeam;

public final class PlayerDamageByPlayerListener implements EventListener {

    @ListenerData(
        event = EntityDamageByEntityEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        if (!(defaultEvent instanceof EntityDamageByEntityEvent event)) {
            return;
        }

        if (!(event.getDamager() instanceof Player damager && event.getEntity() instanceof Player victim)) {
            return;
        }

        final GameMap map = GameStorage.getStorage().getGame(damager.getUniqueId());

        if (map == null) {
            return;
        }

        final BaseTeam playerTeam = map.getTeamPerPlayer().get(damager);
        final Set<Player> playersInTeam = map.getPlayersInTeam().get(playerTeam);

        if (playersInTeam.contains(victim)) {
            event.setCancelled(true);
            event.setDamage(0);
        }
    }
}