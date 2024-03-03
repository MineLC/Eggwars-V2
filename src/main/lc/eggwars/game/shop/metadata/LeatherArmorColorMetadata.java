package lc.eggwars.game.shop.metadata;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.teams.BaseTeam;

public final class LeatherArmorColorMetadata implements ItemMetaData {

    @Override
    public ItemStack parse(ItemStack item, Player player) {      
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            return item;
        }
        final BaseTeam team = game.getTeamPerPlayer().get(player);
        if (team == null) {
            return item;
        }
        final ItemMeta meta = item.getItemMeta();
        if (!(item instanceof LeatherArmorMeta leather)) {
            return item;
        }
        leather.setColor(team.getLeatherColor());
        item.setItemMeta(meta);
        return item;
    }
}