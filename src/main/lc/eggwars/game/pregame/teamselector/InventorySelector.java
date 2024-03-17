package lc.eggwars.game.pregame.teamselector;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.inventory.internal.CustomInventoryHolder;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.IntegerUtils;

public final class InventorySelector {

    public InventoryData createBaseInventory(final Collection<BaseTeam> teams, String title, final MapData data) {
        final int rows = IntegerUtils.aproximate(teams.size(), 9);

        final Inventory inventory = Bukkit.createInventory(
            new CustomInventoryHolder(String.valueOf(data.hashCode())),
            9 * rows,
            title.replace("%max-players%", String.valueOf(data.getMaxPersonsPerTeam())));

        final IntObjectHashMap<BaseTeam> teamsPerSlot = new IntObjectHashMap<>();

        int slot = 0;
        for (final BaseTeam team : teams) {
            final ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
            final LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();

            meta.setDisplayName(team.getName());
            meta.setColor(team.getLeatherColor());
            item.setItemMeta(meta);

            teamsPerSlot.put(slot, team);
            inventory.setItem(slot++, item);
        }
        return new InventoryData(teamsPerSlot, inventory);
    }

    public record InventoryData(IntObjectHashMap<BaseTeam> teamsPerSlot, Inventory inventory) {}
}