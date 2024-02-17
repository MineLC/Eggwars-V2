package lc.eggwars.listeners.shopkeepers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import lc.eggwars.game.GameMap;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shopkeepers.ShopkeepersData;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;
import lc.lcspigot.events.PreInteractEntityEvent;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PreInteractWithEntityListener implements EventListener {

    @ListenerData(
        event = PreInteractEntityEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PreInteractEntityEvent event = (PreInteractEntityEvent)defaultEvent;
        final Player player = event.getPlayer();
        player.sendMessage("INTERACUTADO");
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());

        if (map == null || map.getState() != GameState.IN_GAME) {
            return;
        }
        final int[] shopKeepersID = map.getShopIDs();

        for (final int id : shopKeepersID) {
            event.getPlayer().sendMessage("ENTITYID: " + event.getEntityID() + " ID: " + id);
            if (id == event.getEntityID()) {
                final PlayerData data = PlayerStorage.getInstance().get(player.getUniqueId());
                final ShopkeepersData.Skin skin = ShopKeepersStorage.getInstance().getSkin(data.getShopSkinID());

                if (skin != null) {
                    player.sendMessage(skin.message());
                }
                player.openInventory(ShopKeepersStorage.getInstance().getData().inventory());
            }
        }
    }
}