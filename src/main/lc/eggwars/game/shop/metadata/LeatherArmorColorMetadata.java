package lc.eggwars.game.shop.metadata;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.teams.GameTeam;

public final class LeatherArmorColorMetadata implements ItemMetaData {

    @Override
    public ItemStack parse(ItemStack item, Player player) {      
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            return item;
        }
        final GameTeam team = game.getTeamPerPlayer().get(player);
        if (team != null) {
            setColor(item, player, team.getBase().getLeatherColor());
        }
        return item;
    }

    public void setColor(final ItemStack leatherItem, final Player player, final Color color) {
        final ItemMeta meta = leatherItem.getItemMeta();
        if (!(meta instanceof LeatherArmorMeta leather)) {
            return;
        }
        leather.setColor(color);
        leatherItem.setItemMeta(leather);
    }
}