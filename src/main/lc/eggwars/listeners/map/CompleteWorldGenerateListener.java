package lc.eggwars.listeners.map;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.others.pregameitems.PregameItemsStorage;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import net.swofty.swm.api.events.PostGenerateWorldEvent;

public final class CompleteWorldGenerateListener implements EventListener {

    @ListenerData(
        event = PostGenerateWorldEvent.class,
        priority = EventPriority.HIGHEST
    )
    public void handle(Event defaultEvent) {
        if (MapStorage.getStorage() == null) {
            return;
        }
        final PostGenerateWorldEvent event = (PostGenerateWorldEvent)defaultEvent;
        final String worldName = event.getSlimeWorld().getName();
        final Set<Player> playersTryingJoin = MapStorage.getStorage().getWorldsCurrentlyLoading().get(worldName);

        if (playersTryingJoin == null) {
            return;
        }
        if (playersTryingJoin.isEmpty()) {
            CompletableFuture.runAsync(() -> event.getSlimeWorld().unloadWorld(false));
            MapStorage.getStorage().getWorldsCurrentlyLoading().remove(worldName);
            return;
        }

        final World bukkitWorld = Bukkit.getWorld(worldName);
        final MapData map = MapStorage.getStorage().getMapData(worldName);
        final GameInProgress gameInProgress = new GameInProgress(map, bukkitWorld);

        MapStorage.getStorage().getWorldsCurrentlyLoading().remove(worldName);
        MapStorage.getStorage().loadClickableBlocks(bukkitWorld);

        map.setGame(gameInProgress);

        for (final Player player : playersTryingJoin) {    
            GameStorage.getStorage().join(bukkitWorld, gameInProgress, player);
            PregameItemsStorage.getStorage().send(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(gameInProgress.getWorld().getSpawnLocation());
        }
    }
}