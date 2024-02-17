package lc.eggwars.game.shopkeepers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.inventory.InventoryCreator;
import lc.eggwars.inventory.InventoryCreator.Item;
import lc.eggwars.utils.Chat;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.World;

public final class StartShopkeepers {

    public void load(final EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("inventories/shopkeepers");
        final Set<String> mobs = config.getKeys(false);
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = new InventoryCreator(plugin.getConfig()).create("shopkeeperskins", "shopkeepers");

        final IntObjectHashMap<ShopkeepersData.Skin> inventoryItems = new IntObjectHashMap<>();
        final IntObjectHashMap<ShopkeepersData.Skin> skinsPerID = new IntObjectHashMap<>();

        final World world = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle();

        for (final String mob : mobs) {
            if (!(EntityTypes.createEntityByName(mob, world) instanceof EntityLiving entity)) {
                plugin.getLogger().warning("A entity with the name " + mob + " don't exist.");
                inventoryItems.put(config.getInt(mob + ".slot"),  new ShopkeepersData.Skin(120, "null message on click", -1));
                continue;
            }
            final String mobPath = mob + '.';
            final String message = Chat.color(config.getString(mobPath + "click-send"));
            final Item item = creator.create(mob);
            final ShopkeepersData.Skin skin = new ShopkeepersData.Skin(EntityTypes.a(entity), message, config.getInt(mobPath + "addHeight"));

            inventory.setItem(item.slot(), item.item());
            inventoryItems.put(item.slot(), skin);
            skinsPerID.put(skin.id(), skin);
        }

        ShopKeepersStorage.update(new ShopKeepersStorage(
            Chat.color(plugin.getConfig().getString("shopkeepers.name")),
            skinsPerID,
            new ShopkeepersData(inventory, inventoryItems)
        ));
    }
}