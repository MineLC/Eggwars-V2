package lc.eggwars.inventory;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lc.eggwars.messages.Messages;

public final class InventoryCreator {
    private final FileConfiguration config;

    public InventoryCreator(FileConfiguration config) {
        this.config = config;
    }

    public Inventory create(final String name, final String path) {
        final String finalPath = path + '.';
        return Bukkit.createInventory(
            new CustomInventoryHolder(name.hashCode()),
            config.getInt(finalPath + "rows") * 9,
            Messages.color(config.getString(finalPath + "title")));
    }

    public Item create(String path) {
        final String finalPath = path + '.';
        final String material = config.getString(finalPath + "item");
        final int slot = config.getInt(finalPath + "slot");
        int id = 0;
        short data = 0;
        if (material == null) {
            id = Material.STONE.getId();
            data = 0;
        } else {
            final String[] idAndData = material.split(":");
            id = Integer.parseInt(idAndData[0]);
                
            if (idAndData.length == 2) {
                data = Short.parseShort(idAndData[1]);
            }
        }
        final ItemStack itemStack = new ItemStack(id, 1, data);
        final ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(Messages.color(config.getString(finalPath + "name")));
        List<String> lore = config.getStringList(finalPath + "lore");
        if (!lore.isEmpty()) {
            meta.setLore(lore.stream().map( (string) -> Messages.color(string) ).toList());
        }
        itemStack.setItemMeta(meta);

        return new Item(slot, itemStack);
    }

    public static final record Item(int slot, ItemStack item) {}
}
