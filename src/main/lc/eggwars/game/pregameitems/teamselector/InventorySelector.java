package lc.eggwars.game.pregameitems.teamselector;

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

public final class InventorySelector {

    public InventoryData createBaseInventory(final Collection<BaseTeam> teams, String title, final MapData data) {
        final Inventory inventory = Bukkit.createInventory(
            new CustomInventoryHolder(String.valueOf(data.hashCode())),
            9 * getManyRows(teams.size()),
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

    private int getManyRows(final int itemsAmount) {
        if (itemsAmount / 9 <= 0) {
            return 1;
        }
        return (itemsAmount % 9) == 0
            ? (itemsAmount / 9)
            : (itemsAmount / 9) + 1;
    }

    public record InventoryData(IntObjectHashMap<BaseTeam> teamsPerSlot, Inventory inventory) {}
}