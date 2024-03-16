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
import lc.eggwars.game.pregameitems.PregameItemsStorage;
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
        if (MapStorage.getStorage() == null) {
            return;
        }
        final String worldName = event.getSlimeWorld().getName();
        final Set<Player> playersTryingJoin = MapStorage.getStorage().getWorldsCurrentlyLoading().get(worldName);

        if (playersTryingJoin == null) {
            return;
        }

        MapStorage.getStorage().getWorldsCurrentlyLoading().remove(worldName);

        if (playersTryingJoin.isEmpty()) {
            CompletableFuture.runAsync(() -> event.getSlimeWorld().unloadWorld(false));
            return;
        }

        final World bukkitWorld = Bukkit.getWorld(worldName);
        final MapData map = MapStorage.getStorage().getMapData(worldName);
        final GameInProgress gameInProgress = new GameInProgress(map, bukkitWorld);

        MapStorage.getStorage().loadClickableBlocks(bukkitWorld);

        map.setGame(gameInProgress);

        for (final Player player : playersTryingJoin) {    
            GameStorage.getStorage().join(bukkitWorld, gameInProgress, player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(gameInProgress.getWorld().getSpawnLocation());
            PregameItemsStorage.getStorage().send(player);
        }
    }
}