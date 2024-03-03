package lc.eggwars.listeners.gameshop;


import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.shop.Shop;
import lc.eggwars.game.shop.metadata.ItemMetaData;
import lc.eggwars.game.shop.metadata.LeatherArmorColorMetadata;
import lc.eggwars.inventory.internal.CustomInventoryHolder;
import lc.eggwars.utils.InventoryUtils;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class GameShopInventoryClickListener implements EventListener {
    private final IntObjectHashMap<Shop> shops;

    public GameShopInventoryClickListener(IntObjectHashMap<Shop> shops) {
        this.shops = shops;
    }

    @ListenerData(
        event = InventoryClickEvent.class,
        priority = EventPriority.NORMAL
    )
    public void handle(Event defaultEvent) {
        final InventoryClickEvent event = (InventoryClickEvent)defaultEvent; 
        if (event.getClickedInventory() == null
            || !(event.getClickedInventory().getHolder() instanceof CustomInventoryHolder)
            || (event.getAction() != InventoryAction.PICKUP_ALL
                && event.getAction() != InventoryAction.PICKUP_HALF
                && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            return;
        }

        final Shop shop = shops.get(event.getClickedInventory().getHolder().hashCode());
        if (shop == null) {
            return;
        }

        final Shop.Item item = shop.items().get(event.getSlot());
        if (item == null) {
            return;
        }
        event.setCancelled(true);
        final Player player = (Player)event.getWhoClicked();
        final PlayerInventory inventory = ((CraftPlayer)player).getHandle().inventory;

        final int amount = InventoryUtils.getAmount(item.needItem(), inventory);
        if (amount < item.needAmount()) {
            player.sendMessage("Necesitas: " + item.needAmount() + " items, en total, para comprarlo");
            return;
        }

        if (!item.stackeable()) {
            final int firstEmpty = InventoryUtils.firstEmpty(inventory);
            if (firstEmpty == -1) {
                player.sendMessage("Limpia tu inventario oe pe");
                return;
            }
            InventoryUtils.removeAmount(item.needAmount(), item.needItem(), inventory);
            inventory.items[firstEmpty] = getItem(item.meta(), item.buyItem(), player);
            player.sendMessage("Item comprados");
            return;
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
            player.sendMessage("La cantidad a comprar no cabe en tu inventario");
            return;
        }

        InventoryUtils.removeAmount(itemsToRemove, item.needItem(), inventory);
        InventoryUtils.addItem(item.buyItem(), itemsToBuy, inventory);
    }

    private ItemStack getItem(final ItemMetaData meta, final ItemStack fallbackItem, final Player player) {
        if (meta instanceof LeatherArmorColorMetadata) {
            return CraftItemStack.asNMSCopy(meta.parse(CraftItemStack.asBukkitCopy(fallbackItem), player));
        }
        return fallbackItem.cloneItemStack();
    }
}