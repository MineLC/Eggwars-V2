package lc.eggwars.listeners.map;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.mapsystem.MapStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import net.swofty.swm.api.events.PostGenerateWorldEvent;

public final class CompleteWorldGenerateListener implements EventListener {

    @ListenerData(
        event = PostGenerateWorldEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        final PostGenerateWorldEvent event = (PostGenerateWorldEvent)defaultEvent;
        final String worldName = event.getSlimeWorld().getName();
        final Set<Player> players = MapStorage.getStorage().getWorldsCurrentlyLoading().get(worldName);

        if (players == null) {
            if (MapStorage.getStorage().getWorldsThatNeedUnload().contains(worldName)) {
                EggwarsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(
                    EggwarsPlugin.getInstance(),
                    () -> event.getSlimeWorld().unloadWorld(false));
            }
            return;
        }

        final World bukkitWorld = Bukkit.getWorld(worldName);
        final MapData map = MapStorage.getStorage().getMapData(worldName);
        final GameInProgress gameInProgress = new GameInProgress(map, bukkitWorld);

        map.setGame(gameInProgress);

        for (final Player player : players) {    
            GameStorage.getStorage().join(bukkitWorld, gameInProgress, player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(gameInProgress.getWorld().getSpawnLocation());
        }
        MapStorage.getStorage().getWorldsCurrentlyLoading().remove(worldName);
        MapStorage.getStorage().loadClickableBlocks(bukkitWorld);
    }
}