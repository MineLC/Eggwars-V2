package lc.eggwars.listeners;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import lc.eggwars.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public class PlayerBreakListener implements EventListener {

    @ListenerData(
        priority = EventPriority.LOWEST,
        event = BlockBreakEvent.class
    )
    public void handle(Event defaultEvent) {
        final BlockBreakEvent event = (BlockBreakEvent)defaultEvent;
        if (!SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            return;
        }
        final Material material = event.getBlock().getType();
        if (material == Material.GLASS) {
            event.setCancelled(false);
            return;
        }
        event.setCancelled(true);
    } 
}
