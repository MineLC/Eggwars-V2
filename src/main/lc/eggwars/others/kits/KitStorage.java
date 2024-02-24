package lc.eggwars.others.kits;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import lc.eggwars.inventory.types.KitInventory;
import lc.eggwars.players.PlayerStorage;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final record KitStorage(KitInventory inventory) {

    private static KitStorage storage;

    public static KitStorage getStorage() {
        return storage;
    }

    public void setKit(final Player player, final boolean clearInventory) {
        final Kit selectedKit = PlayerStorage.getStorage().get(player.getUniqueId()).getKit();
        if (selectedKit == null) {
            return;
        }

        final PlayerInventory inventory = ((CraftPlayer)player).getHandle().inventory;

        if (clearInventory) {
            for (int i = 0; i < inventory.items.length; i++) {
                inventory.items[i] = null;
            }
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