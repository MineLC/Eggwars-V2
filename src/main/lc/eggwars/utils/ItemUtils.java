package lc.eggwars.utils;

import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class ItemUtils {

    public static int addItem(final ItemStack item, final int amount, final PlayerInventory inventory) {
        final ItemStack[] items = inventory.items;
        int amountItems = amount;
        int freeSlot = -1;

        for (final ItemStack nmsItem : items) {
            if (nmsItem == null) {
                if (freeSlot != -1) {
                    continue;
                }
                freeSlot++;
                continue;
            }

            if (nmsItem.count >= 64) {
                continue;
            }

            if (nmsItem.getItem() == item.getItem()) {
                if (amountItems + nmsItem.count <= 64) {
                    nmsItem.count = nmsItem.count + amountItems;
                    return 0;
                }
                amountItems = amountItems - (64 - nmsItem.count);
                nmsItem.count = 64;

                if (amountItems <= 0) {
                    return 0;
                }
            }
        }

        if (amountItems == 0) {
            return 0;
        }

        for (int i = freeSlot; i < items.length; i++) {
            if (items[i] != null) {
                continue;
            }

            if (amountItems >= 64) {
                amountItems -= 64;
                items[i] = new ItemStack(item.getItem(), 64);
                if (amountItems >= 64) {
                    continue;
                }
                break;
            }
            items[i] = new ItemStack(item.getItem(), amountItems);
            break;
        }

        return amountItems;
    }
}