package lc.eggwars.inventory.types;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import lc.eggwars.game.shop.Shop;
import lc.eggwars.game.shop.ShopsData;
import lc.eggwars.game.shop.metadata.ItemMetaData;
import lc.eggwars.game.shop.metadata.LeatherArmorColorMetadata;
import lc.eggwars.messages.Messages;
import lc.eggwars.utils.InventoryUtils;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class GameShopInventory {
    private final ShopsData data;

    public GameShopInventory(ShopsData data) {
        this.data = data;
    }

    public boolean handle(InventoryClickEvent event) { 
        final Shop shop = data.shops().get(event.getInventory().getHolder().hashCode());
        if (shop == null) {
            return false;
        }
        final Inventory shopItemInventory = data.shopsHeader().get(event.getSlot());

        if (shopItemInventory != null) {
            event.setCancelled(true);
            if (!shopItemInventory.getHolder().equals(event.getInventory().getHolder())) {
                event.getWhoClicked().openInventory(shopItemInventory);
            }
            return false;
        }

        final Shop.Item item = shop.items().get(event.getSlot());
        if (item == null) {
            return false;
        }
        event.setCancelled(true);
        final Player player = (Player)event.getWhoClicked();
        final PlayerInventory inventory = ((CraftPlayer)player).getHandle().inventory;

        final int amount = InventoryUtils.getAmount(item.needItem(), inventory);
        if (amount < item.needAmount()) {
            Messages.send(player, "gameshop.need-items");
            return true;
        }

        if (!item.stackeable()) {
            final int firstEmpty = InventoryUtils.firstEmpty(inventory);
            if (firstEmpty == -1) {
                Messages.send(player, "gameshop.clean-inventory");
                return true;
            }
            InventoryUtils.removeAmount(item.needAmount(), item.needItem(), inventory);
            inventory.items[firstEmpty] = getItem(item.meta(), item.buyItem(), player);
            buySound(player);
            return true;
        }

        final int itemsToRemove;
        final int itemsToBuy;

        if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            itemsToRemove = item.needAmount();
            itemsToBuy = item.buyItem().count;
        } else {
            final int buyAmount = (amount / item.needAmount());
            itemsToRemove = item.needAmount() * buyAmount;
            itemsToBuy = item.buyItem().count * buyAmount;
        }

        if (!InventoryUtils.canAdd(item.buyItem(), itemsToBuy, inventory)) {
            Messages.send(player, "gameshop.no-enough-space");
            return true;
        }

        InventoryUtils.removeAmount(itemsToRemove, item.needItem(), inventory);
        InventoryUtils.addItem(item.buyItem(), itemsToBuy, inventory);
        buySound(player);
        return true;
    }

    private void buySound(final Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        final PacketPlayOutNamedSoundEffect itemPickupSound = new PacketPlayOutNamedSoundEffect("random.pop", entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, 1.0f, 1.0f);
        entityPlayer.playerConnection.networkManager.handle(itemPickupSound);
    }

    private ItemStack getItem(final ItemMetaData meta, final ItemStack fallbackItem, final Player player) {
        if (meta instanceof LeatherArmorColorMetadata) {
            return CraftItemStack.asNMSCopy(meta.parse(CraftItemStack.asBukkitCopy(fallbackItem), player));
        }
        return fallbackItem.cloneItemStack();
    }
}