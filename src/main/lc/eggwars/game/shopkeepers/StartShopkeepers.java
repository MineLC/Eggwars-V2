package lc.eggwars.game.shopkeepers;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.EggwarsPlugin;
import lc.eggwars.utils.Chat;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.World;

public final class StartShopkeepers {

    public void load(final EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("shopkeepers");
        final Set<String> mobs = config.getKeys(false);
        final Inventory inventory = Bukkit.createInventory(
            new CustomInventoryHolder("shopkeeperskins".hashCode()),
            plugin.getConfig().getInt("shopkeepers.rows") * 9,
            Chat.color(plugin.getConfig().getString("shopkeepers.title")));

        final IntObjectHashMap<ShopkeepersData.Skin> inventoryItems = new IntObjectHashMap<>();
        final IntObjectHashMap<ShopkeepersData.Skin> skinsPerID = new IntObjectHashMap<>();

        final World world = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle();

        for (final String mob : mobs) {

            if (!(EntityTypes.createEntityByName(mob, world) instanceof EntityLiving entity)) {
                System.out.println("La entidad " + mob + " no es un mob.");
                inventoryItems.put(config.getInt(mob + ".slot"),  new ShopkeepersData.Skin(120, "null message on click", -1));
                continue;
            }

            final String mobPath = mob + '.';
            final String message = Chat.color(config.getString(mobPath + "click-send"));
            final String material = config.getString(mobPath + "item");
            final int slot = config.getInt(mobPath + "slot");
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

            final ShopkeepersData.Skin skin = new ShopkeepersData.Skin(EntityTypes.a(entity), message, config.getInt(mobPath + "addHeight"));
            final ItemStack itemStack = new ItemStack(id, 1, data);
            final ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(Chat.color(config.getString(mobPath + "name")));
            List<String> lore = config.getStringList(mobPath + "lore");
            if (!lore.isEmpty()) {
                meta.setLore(lore.stream().map( (string) -> Chat.color(string) ).toList());
            }
            itemStack.setItemMeta(meta);

            inventory.setItem(slot, itemStack);
            inventoryItems.put(slot, skin);
            skinsPerID.put(skin.id(), skin);
        }

        ShopKeepersStorage.update(new ShopKeepersStorage(skinsPerID, new ShopkeepersData(inventory, inventoryItems)));
    }
}