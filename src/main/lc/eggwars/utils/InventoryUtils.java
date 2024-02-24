package lc.eggwars.utils;

import org.bukkit.inventory.Inventory;

import lc.eggwars.inventory.internal.CustomInventoryHolder;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class InventoryUtils {

    public static int getId(final Inventory inventory) {
        if (inventory.getHolder() == null) {
            return -1;
        }
        if (inventory.getHolder() instanceof CustomInventoryHolder custom) {
            return custom.hashCode();
        }
        return -1;
    }

    public static int getAmount(final ItemStack item, final PlayerInventory inventory) {
        final ItemStack[] items = inventory.items;
        int amount = 0;

        for (final ItemStack nmsItem : items) {
            if (nmsItem == null) {
                continue;
            }
            if (nmsItem.getItem() == item.getItem()) {
                amount += nmsItem.count;
            }
        }
        return amount;
    }

    public static void removeAmount(final int amount, final ItemStack item, final PlayerInventory inventory) {
        if (amount == 0) {
            return;
        }
        final ItemStack[] items = inventory.items;
        int removedAmount = 0;
        int index = 0;

        for (final ItemStack nmsItem : items) {
            if (nmsItem == null || nmsItem.getItem() != item.getItem()) {
                index++;
                continue;
            }
            removedAmount += nmsItem.count;

            if (removedAmount == amount) {
                inventory.items[index] = null;
                return;
            }

            if (removedAmount > amount) {
                nmsItem.count = (removedAmount - amount);
                return;
            }
            inventory.items[index++] = null;
        }
        inventory.update();
    }

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
        
        if (freeSlot == -1) {
            return amountItems;
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
                return 0;
            }
            items[i] = new ItemStack(item.getItem(), amountItems);
            return 0;
        }

        return amountItems;
    }

    public static boolean canAdd(final ItemStack item, final int amount, final PlayerInventory inventory) {
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
                    return true;
                }
                amountItems = amountItems - (64 - nmsItem.count);

                if (amountItems <= 0) {
                    return true;
                }
            }
        }

        if (amountItems == 0) {
            return true;
        }
        if (freeSlot == -1) {
            return false;
        }

        for (int i = freeSlot; i < items.length; i++) {
            if (items[i] != null) {
                continue;
            }

            if (amountItems >= 64) {
                amountItems -= 64;
                if (amountItems >= 64) {
                    continue;
                }
                return true;
            }
            return true;
        }

        return false;
    }

    public static int firstEmpty(final PlayerInventory inventory) {
        final ItemStack[] items = inventory.items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public static void clear(final PlayerInventory inventory) {
        final ItemStack[] items = inventory.items;
        for (int i = 0; i < items.length; i++) {
            items[i] = null;
        }
    }
}