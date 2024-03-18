package lc.eggwars.listeners;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.lcspigot.listeners.ListenerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import lc.lcspigot.listeners.EventListener;

public class PlayerSaturationEvent implements EventListener {

    @ListenerData(
            event = FoodLevelChangeEvent.class,
            priority = EventPriority.LOWEST
    )
    public void handle(Event event) {
        final FoodLevelChangeEvent e = (FoodLevelChangeEvent)event;
        Player p = (Player) e.getEntity();
        GameInProgress game = GameStorage.getStorage().getGame(p.getUniqueId());
        if(game == null){
            e.setCancelled(true);
            return;
        }
        if(game.getState() == GameState.PREGAME)
            e.setCancelled(true);

    }
}
