package lc.eggwars.game.clickable;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shopkeepers.ShopkeepersData;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;
import lc.eggwars.utils.ClickableBlock;

public final class ClickableShopkeeper implements ClickableBlock {

    @Override
    public void onClick(Player player, Action action) {
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final PlayerData data = PlayerStorage.getInstance().get(player.getUniqueId());
        final ShopkeepersData.Skin skin = ShopKeepersStorage.getInstance().getSkin(data.getShopSkinID());
        if (skin != null) {
            player.sendMessage(skin.message());
        }
        player.openInventory(ShopKeepersStorage.getInstance().getData().inventory());
    }
}