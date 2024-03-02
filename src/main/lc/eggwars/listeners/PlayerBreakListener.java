package lc.eggwars.listeners;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import lc.eggwars.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public class PlayerBreakListener implements EventListener {

    @ListenerData(
        priority = EventPriority.LOWEST,
        event = BlockBreakEvent.class
    )
    public void handle(Event arg0) {
        final BlockBreakEvent event = (BlockBreakEvent)arg0;
        if (event.getPlayer().getWorld().equals(SpawnStorage.getStorage().location().getWorld())) {
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
