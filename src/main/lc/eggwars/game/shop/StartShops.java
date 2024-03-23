package lc.eggwars.game.shop;

import java.io.File;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.tinylog.Logger;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.shop.metadata.ItemMetaData;
import lc.eggwars.game.shop.metadata.LeatherArmorColorMetadata;
import lc.eggwars.inventory.internal.CustomInventoryHolder;
import lc.eggwars.inventory.internal.InventoryCreator;
import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.messages.Messages;
import lc.eggwars.utils.IntegerUtils;

public class StartShops {
    
    public ShopsData load(final EggwarsPlugin plugin) {
        tryLoadDefaultShops(plugin);

        final HeaderShop[] headerItems = createHeader(plugin);
        final IntObjectHashMap<Shop> shopsPerId = new IntObjectHashMap<>(headerItems.length);
        final IntObjectHashMap<Inventory> shopsPerSlot = new IntObjectHashMap<>(headerItems.length);
        final Inventory mainInventory;

        for (final HeaderShop headerShop : headerItems) {
            if (headerShop == null) {
                continue;
            }
            final FileConfiguration shopConfig = tryLoadShop(plugin, headerShop.shopName);
            if (shopConfig == null) {
                Logger.info("Can't found shop file for the header item: " + headerShop.shopName);
                continue;
            }
            final String shopID = ("shop-"+headerShop.shopName);
            final Set<String> shopItems = shopConfig.getKeys(false);
            final int rows = IntegerUtils.aproximate(shopItems.size(), 9);
            final Inventory inventory = Bukkit.createInventory(
                new CustomInventoryHolder(shopID),
                (9 * rows) + 9,
                headerShop.title
            );
            final IntObjectHashMap<Shop.Item> items = createItems(headerItems, shopConfig, shopItems, inventory);
            final Shop shop = new Shop(inventory, items);
            shopsPerId.put(shopID.hashCode(), shop);
            shopsPerSlot.put(headerShop.slot, inventory);
        }
        mainInventory = shopsPerSlot.get(0);

        return new ShopsData(mainInventory, shopsPerId, shopsPerSlot);
    }

    private HeaderShop[] createHeader(final EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("shop");
        final Set<String> header = config.getConfigurationSection("header").getKeys(false);
        final InventoryCreator creator = new InventoryCreator(config);

        final HeaderShop[] headerItems = new HeaderShop[header.size()];
        int index = 0;

        for (final String shopSection : header) {
            final Item item = creator.create("header." + shopSection);
            if (item.slot() < 0 || item.slot() > 9) {
                Logger.info("Header item slot need be 0-9");
                index++;
                continue;
            }
            headerItems[index++] = new HeaderShop(item, shopSection, Messages.color(config.getString(shopSection + ".title")), item.slot());
        }
        return headerItems;
    }

    private FileConfiguration tryLoadShop(final EggwarsPlugin plugin, final String shopName) {
        final File shopFile = new File(plugin.getDataFolder(), "shops/" + shopName + ".yml");
        if (!shopFile.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(shopFile);
    }

    private IntObjectHashMap<Shop.Item> createItems(final HeaderShop[] headerItems, final FileConfiguration config, final Set<String> shopItems, final Inventory shopInventory) {
        final InventoryCreator creator = new InventoryCreator(config);
        final IntObjectHashMap<Shop.Item> items = new IntObjectHashMap<>(shopItems.size());

        for (final HeaderShop headerShop : headerItems) {
            shopInventory.setItem(headerShop.item.slot(), headerShop.item.item());
        }

        for (final String shopitem : shopItems) {
            final Item item = creator.create(shopitem);
            final int realSlot = item.slot() + 9;
            if (realSlot >= shopInventory.getSize()) {
                Logger.warn("The slot of the shopitem " + shopitem + " can't be more than inventory size");
                continue;
            }
            shopInventory.setItem(realSlot, item.item());
            final ItemStack itemStack = item.item();
            final ItemMeta meta = itemStack.getItemMeta();
            meta.setLore(null);
            itemStack.setItemMeta(meta);

            final net.minecraft.server.v1_8_R3.ItemStack buyItem = CraftItemStack.asNMSCopy(itemStack);
            final net.minecraft.server.v1_8_R3.ItemStack needItem = CraftItemStack.asNMSCopy(creator.getItem(shopitem + ".need-item"));
            items.put(realSlot, new Shop.Item(buyItem, needItem, needItem.count, config.getBoolean(shopitem + ".stackeable"), createMetadata(item.item())));
        }
        return items;
    }

    private void tryLoadDefaultShops(final EggwarsPlugin plugin) {
        if (new File(plugin.getDataFolder(), "shops").exists()) {
            return;
        }
        plugin.tryCreateFiles("shops/swords.yml", "shops/armor.yml", "shops/blocks.yml", "shops/food.yml", "shops/tools.yml");
    }

    private ItemMetaData createMetadata(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta) {
            return new LeatherArmorColorMetadata();
        }
        return null;
    }

    private record HeaderShop(Item item, String shopName, String title, int slot) {}

}