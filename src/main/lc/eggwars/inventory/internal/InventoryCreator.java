package lc.eggwars.inventory.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tinylog.Logger;

import lc.eggwars.messages.Messages;
import lc.eggwars.utils.IntegerUtils;

public final class InventoryCreator {
    private final FileConfiguration config;

    public InventoryCreator(FileConfiguration config) {
        this.config = config;
    }

    public Inventory create(final String name, final String path) {
        final String finalPath = path + '.';
        return Bukkit.createInventory(
            new CustomInventoryHolder(name),
            config.getInt(finalPath + "rows") * 9,
            Messages.color(config.getString(finalPath + "title")));
    }

    public Item create(final String path) {
        final String finalPath = path + '.';
        final int slot = config.getInt(finalPath + "slot");
    
        return new Item(slot, getItem(path));
    }

    public ItemStack getItem(final String path) {
        final String finalPath = path + '.';

        final ItemStack itemStack = createBase(config.getString(finalPath + "item"), config.getInt(finalPath + "amount"));
        final ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(Messages.color(config.getString(finalPath + "name")));
        final List<String> lore = getLore(config.getStringList(finalPath + "lore"));
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        setEnchantments(config.getStringList(finalPath + "enchantments"), meta);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack createBase(final String materialString, int amount) {
        if (amount <= 0) {
            amount = 1;
        }
        if (materialString == null) {
            return new ItemStack(Material.STONE, amount);
        }
        final String[] idAndData = StringUtils.split(materialString, ':');
        if (idAndData.length == 0) {
            return new ItemStack(Material.STONE, amount);    
        }
        final Material material = Material.getMaterial(IntegerUtils.parsePositive(idAndData[0]));
        final short data = (idAndData.length == 2) ? Short.parseShort(idAndData[1]) : 0;
        return new ItemStack((material == null) ? Material.STONE : material, amount, data);
    }

    private void setEnchantments(final List<String> enchantments, final ItemMeta meta) {
        for (final String enchantmentString : enchantments) {
            final String[] split = StringUtils.split(enchantmentString, ':');
            if (split.length != 2) {
                Logger.warn("The enchantment: " + enchantmentString + " need the format: Enchantment:Level");
            }
            final Enchantment enchantment = Enchantment.getByName(split[0]);
            if (enchantment == null) {
                Logger.warn("The enchant type: " + split[0] + " don't exist");
                continue;
            }
            int level = IntegerUtils.parsePositive(split[1]);
            meta.addEnchant(enchantment, (level <= 0) ? 1 : level, false);
        }
    }

    private List<String> getLore(final List<String> base) {
        final List<String> list = new ArrayList<>(base.size());
        for (final String line : base) {
            list.add(Messages.color(line));
        }
        return list;
    }
    public static final record Item(int slot, ItemStack item) {}
}