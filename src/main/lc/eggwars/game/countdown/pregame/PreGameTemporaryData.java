package lc.eggwars.game.countdown.pregame;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.shop.metadata.LeatherArmorColorMetadata;
import lc.eggwars.teams.BaseTeam;

public final class PreGameTemporaryData {
    private final Inventory teamSelector;
    private final IntObjectHashMap<BaseTeam> teamInventorySlots;

    public PreGameTemporaryData(Inventory teamSelector, IntObjectHashMap<BaseTeam> teamInventorySlots) {
        this.teamSelector = teamSelector;
        this.teamInventorySlots = teamInventorySlots;
    }

    public boolean joinToTeam(final Player player, final GameInProgress game, final BaseTeam team) {
        final BaseTeam baseTeam = game.getTeamPerPlayer().get(player);
        if (baseTeam != null) {
            if (baseTeam.equals(team)) {
                return false;
            }
            game.getPlayersInTeam().get(baseTeam).remove(player);
            game.getTeamPerPlayer().remove(player);
        }
        game.getTeamPerPlayer().put(player, team);
        game.getPlayersInTeam().get(team).add(player);

        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        new LeatherArmorColorMetadata().setColor(chestplate, player, team.getLeatherColor());

        player.getInventory().setChestplate(chestplate);
        return true;
    }

    public void leave(final Player player, final GameInProgress game) {
        final BaseTeam team = game.getTeamPerPlayer().get(player);
        if (team != null) {
            game.getPlayersInTeam().get(team).remove(player);
        }
        game.getTeamPerPlayer().remove(player);
    }

    public Inventory getTeamSelectorInventory() {
        return teamSelector;
    }

    public BaseTeam getTeam(int clickedSlot) {
        return teamInventorySlots.get(clickedSlot);
    }
}