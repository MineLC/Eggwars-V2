package lc.eggwars.listeners.gameshop;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData;
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
        if (player == null) {
            return;
        }

        if (player.getOpenInventory() != null
            && player.getOpenInventory().getTopInventory() != null
            && player.getOpenInventory().getTopInventory().getHolder().equals(ShopKeepersStorage.getStorage().data().itemsShop().getHolder())) {
            return;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());

        if (game == null || game.getState() != GameState.IN_GAME) {
            return;
        }
        final int shops = game.getMapData().getShopSpawns().length;
        if (event.getEntityID() >= (Integer.MAX_VALUE - shops)){
            final ShopkeepersData.Skin skin = ShopKeepersStorage.getStorage().skins().get(event.getEntityID());
            if (skin != null) {
                player.sendMessage(skin.message());
            }
            player.openInventory(ShopKeepersStorage.getStorage().data().itemsShop());
        }   
    }
}