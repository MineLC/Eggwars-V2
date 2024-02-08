package lc.eggwars.utils;

import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class ItemUtils {

    public static int addItem(final ItemStack item, final int amount, final PlayerInventory inventory) {
        final ItemStack[] items = inventory.items;
        int amountItems = amount;

        for (final ItemStack nmsItem : items) {
            if (nmsItem == null) {
                continue;
            }

            if (nmsItem.count >= 64) {
                continue;
            }

            if (nmsItem.getItem().equals(item.getItem())) {
                if (item.count + nmsItem.count > 64) {
                    amountItems -= nmsItem.count;
                    nmsItem.count = 64;
                    continue;
                }
                nmsItem.count = nmsItem.count + amountItems;
                amountItems = 0;
                return 0;
            }
        }

        if (amountItems == 0) {
            return 0;
        }
        int index = 0;

        for (final ItemStack nmsItem : items) {
            if (nmsItem != null) {
                index++;
                continue;
            }

            if (amountItems >= 64) {
                amountItems -= 64;
                items[index++] = new ItemStack(item.getItem(), 64);
                break;
            }
            items[index++] = new ItemStack(item.getItem(), amountItems);
            break;
        }

        return amountItems;
    }
}