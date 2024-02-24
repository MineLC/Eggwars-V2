package lc.eggwars.listeners.gameshop;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;
import lc.lcspigot.events.PreInteractEntityEvent;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class ShopkeeperListener implements EventListener {

    @ListenerData(
        event = PreInteractEntityEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PreInteractEntityEvent event = (PreInteractEntityEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameInProgress map = GameStorage.getStorage().getGame(player.getUniqueId());

        if (map == null || map.getState() != GameState.IN_GAME) {
            return;
        }

        final int[] shopKeepersID = map.getMapData().getShopIDs();

        for (final int id : shopKeepersID) {
            if (id == event.getEntityID()) {
                final PlayerData data = PlayerStorage.getStorage().get(player.getUniqueId());
                final ShopkeepersData.Skin skin = ShopKeepersStorage.getStorage().skins().get(data.getShopSkinID());

                if (skin != null) {
                    player.sendMessage(skin.message());
                }
                player.openInventory(ShopKeepersStorage.getStorage().data().inventory());
            }
        }
    }
}