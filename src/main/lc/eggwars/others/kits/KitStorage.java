package lc.eggwars.others.kits;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.inventory.types.KitInventory;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final record KitStorage(KitInventory inventory, IntObjectHashMap<Kit> kitsPerId) {

    private static KitStorage storage;

    public static KitStorage getStorage() {
        return storage;
    }

    public void setKit(final Player player, final boolean clearInventory) {
        final int kitSelectedID = PlayerDataStorage.getStorage().get(player.getUniqueId()).kitSelected;
        final Kit selectedKit = kitsPerId.get(kitSelectedID);
        final PlayerInventory inventory = ((CraftPlayer)player).getHandle().inventory;

        if (clearInventory) {
            for (int i = 0; i < inventory.items.length; i++) {
                inventory.items[i] = null;
            }
        }

        if (selectedKit == null || kitSelectedID == 0) {
            return;
        }

        if (selectedKit.items() != null) {
            int itemsRemain = selectedKit.items().length;
            int index = 0;

            while (itemsRemain > 0) {
                for (int i = 0; i < inventory.items.length; i++) {
                    if (inventory.items[i] != null) {
                        continue;
                    }
                    inventory.items[i] = selectedKit.items()[index++].cloneItemStack();
                    --itemsRemain;
                    break;
                }
            }
        }

        for (int i = 0; i < selectedKit.armor().length; i++) {
            inventory.armor[i] = (selectedKit.armor()[i] == null) ? null : selectedKit.armor()[i].cloneItemStack();
        }

        if (selectedKit.potionEffects() != null) {
            for (final PotionEffect effect : selectedKit.potionEffects()) {
                player.addPotionEffect(effect);
            }
        }
    }

    static void update(KitStorage newStorage) {
        storage = newStorage;
    }
}