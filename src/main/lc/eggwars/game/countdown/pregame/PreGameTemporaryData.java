package lc.eggwars.game.countdown.pregame;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.shop.metadata.LeatherArmorColorMetadata;
import lc.eggwars.teams.BaseTeam;

public final class PreGameTemporaryData {
    private final Inventory teamSelector;
    private final IntObjectHashMap<BaseTeam> teamInventorySlots;
    private final Map<Player, BaseTeam> playersSelectingTeam = new HashMap<>();

    public PreGameTemporaryData(Inventory teamSelector, IntObjectHashMap<BaseTeam> teamInventorySlots) {
        this.teamSelector = teamSelector;
        this.teamInventorySlots = teamInventorySlots;
    }

    public void joinToTeam(final Player player, final BaseTeam team) {
        playersSelectingTeam.remove(player);
        playersSelectingTeam.put(player, team);

        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        new LeatherArmorColorMetadata().setColor(chestplate, player, team.getLeatherColor());

        player.getInventory().setChestplate(chestplate);
    }

    public void leave(final Player player) {
        playersSelectingTeam.remove(player);
    }

    public Inventory getTeamSelectorInventory() {
        return teamSelector;
    }

    public BaseTeam getTeam(int clickedSlot) {
        return teamInventorySlots.get(clickedSlot);
    }

    public Map<Player, BaseTeam> getPlayers() {
        return playersSelectingTeam;
    }
}