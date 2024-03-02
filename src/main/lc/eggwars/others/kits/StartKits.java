package lc.eggwars.others.kits;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.tinylog.Logger;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.internal.InventoryCreator;
import lc.eggwars.inventory.types.KitInventory;
import lc.eggwars.utils.IntegerUtils;
import net.minecraft.server.v1_8_R3.ItemStack;

public final class StartKits {

    final EggwarsPlugin plugin;

    public StartKits(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final File kitsFolder = new File(plugin.getDataFolder(), "kits");
        tryCreateDefaultKits(kitsFolder);

        final FileConfiguration kitsInventory = plugin.loadConfig("inventories/kits");

        final InventoryCreator creator = new InventoryCreator(kitsInventory);
        final Inventory inventory = creator.create("kits", "inventory");

        final File[] kitsFiles = kitsFolder.listFiles();
        final IntObjectHashMap<Kit> kits = new IntObjectHashMap<>();
        final IntObjectHashMap<Kit> kitsPerId = new IntObjectHashMap<>();
        int index = 0;

        for (final File kitFile : kitsFiles) {
            final Kit kit = createKit(YamlConfiguration.loadConfiguration(kitFile));
            kitsPerId.put(kit.name().hashCode(), kit);
            inventory.setItem(kit.inventoryItem().slot(), kit.inventoryItem().item());
            kits.put(index++, kit);
        }
        KitStorage.update(new KitStorage(new KitInventory(kits, inventory), kitsPerId));
    }

    private void tryCreateDefaultKits(File kitsFolder) {
        if (kitsFolder.exists()) {
            return;
        }
        plugin.tryCreateFiles("kits/bunny.yml");
    }

    private Kit createKit(final FileConfiguration config) {
        final InventoryCreator creator = new InventoryCreator(config);
        final String name = config.getString("name");
        return new Kit(
            name.hashCode(),
            name,
            creator.create("inventory-item"),
            createArmor(config, creator),
            createItems(config),
            createPotionEffects(config),
            config.getInt("cost"));
    }

    private ItemStack[] createArmor(final FileConfiguration config, InventoryCreator creator) {
        return new ItemStack[] {
            createArmorPiece(config.getString("armor.boots")),
            createArmorPiece(config.getString("armor.leggings")),
            createArmorPiece(config.getString("armor.chestplate")),
            createArmorPiece(config.getString("armor.heltmet"))
        };
    }

    private ItemStack createArmorPiece(final String section) {
        if (section == null) {
            return null;
        }
        Material material = Material.getMaterial(IntegerUtils.parsePositive(section));
        if (material == null) {
            return null;
        }
        return CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(material));
    }

    private ItemStack[] createItems(final FileConfiguration config) {
        final List<String> itemList = config.getStringList("items");
        if (itemList.isEmpty()) {
            return null;
        }
        final ItemStack[] items = new ItemStack[itemList.size()];
        int index = 0;
    
        for (final String item : itemList) {
            final String[] split = StringUtils.split(item, ':');
            Material material = Material.getMaterial(Integer.parseInt(split[0]));
            if (material == null) {
                material = Material.STONE;
            }
            int amount = 1;
            if (split.length >= 2) {
                int newAmount = Integer.parseInt(split[1]);
                amount = (newAmount == -1) ? 1 : newAmount;
            }
            final org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(material, amount);
            if (split.length == 4) {
                Enchantment enchantment = Enchantment.getByName(split[2]);
                if (enchantment == null) {
                    Logger.warn("The enchant type: " + split[2] + " don't exist");
                } else {
                    int level = Integer.parseInt(split[3]);
                    itemStack.addUnsafeEnchantment(enchantment, (level == 0) ? 1 : level);
                }
            }
            items[index++] = CraftItemStack.asNMSCopy(itemStack);
        }
        return items;
    }

    private PotionEffect[] createPotionEffects(final FileConfiguration config) {
        final List<String> effects = config.getStringList("effects");
        if (effects.isEmpty()) {
            return null;
        }

        final PotionEffect[] potionEffects = new PotionEffect[effects.size()];
        int index = 0;
        for (final String effect : effects) {
            final String[] split = StringUtils.split(effect, ':');
            PotionEffectType type = PotionEffectType.getByName(split[0]);
            int level = 0;
            int duration = 0;

            if (type == null) {
                type = PotionEffectType.ABSORPTION;
                Logger.warn("The potion type: " + split[0] + " don't exist");
            }
            if (split.length >= 2) {
                level = Integer.parseInt(split[1]) - 1;
            }
            if (split.length == 3) {
                duration = Integer.parseInt(split[2]);
            }
            potionEffects[index++] = new PotionEffect(type, duration, level);
        }
        return potionEffects;
    }
}
