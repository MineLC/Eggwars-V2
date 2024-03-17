package lc.eggwars.listeners;

import lc.lcspigot.listeners.ListenerData;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class PlayerChatListener implements Listener {

    @ListenerData(
            priority = EventPriority.LOWEST,
            event = AsyncPlayerChatEvent.class
    )
    public void handle(Event e){
        //Chat global cuando estás en la lobby general de eggwars.
        //chat privado por arena:
        // se divide en estados: cuando los jugadores están en pregame.
        //cuando los jugadorse estan en el game:
        //cuando los jugadores son espectadores, cuando estan por team y cuando es global !
    }
}
