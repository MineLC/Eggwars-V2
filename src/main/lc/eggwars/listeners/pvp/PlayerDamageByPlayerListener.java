package lc.eggwars.listeners.pvp;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.teams.GameTeam;

public final class PlayerDamageByPlayerListener implements EventListener {

    @ListenerData(
        event = EntityDamageByEntityEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        if (!(defaultEvent instanceof EntityDamageByEntityEvent event)) {
            return;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(event.getDamager().getUniqueId());

        if (game == null || game.getState() != GameState.IN_GAME) {
            event.getDamager().sendMessage("No estás en juego");
            return;
        }

        final GameTeam playerTeam = game.getTeamPerPlayer().get(event.getDamager());
        final Set<Player> playersInTeam = playerTeam.getPlayers();

        if (playersInTeam.contains(event.getEntity())) {
            event.getDamager().sendMessage("Pertenece a tu equipo");
            event.setCancelled(true);
            event.setDamage(0);
        }
    }
}