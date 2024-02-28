package lc.eggwars.game.shop.shopkeepers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.Inventory;
import org.tinylog.Logger;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.shop.Shop;
import lc.eggwars.inventory.internal.InventoryCreator;
import lc.eggwars.inventory.internal.InventoryCreator.Item;
import lc.eggwars.messages.Messages;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.World;

public final class StartShopkeepers {

    public void load(final EggwarsPlugin plugin, final IntObjectHashMap<Shop> shops) {
        final FileConfiguration config = plugin.loadConfig("inventories/shopkeepers");
        final Set<String> mobs = config.getKeys(false);
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = new InventoryCreator(plugin.getConfig()).create("shopkeeperskins", "shopkeepers");

        final IntObjectHashMap<ShopkeepersData.Skin> inventoryItems = new IntObjectHashMap<>();
        final IntObjectHashMap<ShopkeepersData.Skin> skinsPerID = new IntObjectHashMap<>();

        final World world = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle();

        for (final String mob : mobs) {
            if (!(EntityTypes.createEntityByName(mob, world) instanceof EntityLiving entity)) {
                Logger.warn("A entity with the name " + mob + " don't exist.");
                inventoryItems.put(config.getInt(mob + ".slot"),  new ShopkeepersData.Skin("Villager", 120, "null message on click", -1, 0));
                continue;
            }
            final String mobPath = mob + '.';
            final String message = Messages.color(config.getString(mobPath + "click-send"));
            final Item item = creator.create(mob);
            final ShopkeepersData.Skin skin = new ShopkeepersData.Skin(
                mob,
                EntityTypes.a(entity),
                message,
                config.getInt(mobPath + "addHeight"),
                config.getInt(mobPath + "cost"));

            inventory.setItem(item.slot(), item.item());
            inventoryItems.put(item.slot(), skin);
            skinsPerID.put(skin.id(), skin);
        }

        ShopKeepersStorage.update(new ShopKeepersStorage(
            Messages.color(plugin.getConfig().getString("shopkeepers.name")),
            skinsPerID,
            new ShopkeepersData(inventory, shops.values().iterator().next().inventory(), inventoryItems)
        ));
    }
}