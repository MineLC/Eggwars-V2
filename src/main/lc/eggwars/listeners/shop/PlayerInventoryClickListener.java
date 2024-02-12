package lc.eggwars.listeners.shop;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.game.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shopkeepers.ShopkeepersData;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.messages.Messages;
import lc.eggwars.players.PlayerData;
import lc.eggwars.players.PlayerStorage;

public class PlayerInventoryClickListener implements EventListener {

    private final EggwarsPlugin plugin;
    private final int removeEntity;

    public PlayerInventoryClickListener(EggwarsPlugin plugin) {
        this.plugin = plugin;
        this.removeEntity = plugin.getConfig().getInt("shopkeepers.preview-seconds-duration") * 20;
    }

    @ListenerData(
        event = InventoryClickEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(final Event defaultEvent) {
        final InventoryClickEvent event = (InventoryClickEvent)defaultEvent;
        final ShopkeepersData data = ShopKeepersStorage.getInstance().getData();
        if (event.getClickedInventory() == null || !data.inventory().getHolder().equals(event.getClickedInventory().getHolder())) {
            return;
        }

        final ShopkeepersData.Skin skinClicked = data.items().get(event.getSlot());
        event.setCancelled(true);
        if (skinClicked == null) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();

        if (event.getAction() != InventoryAction.DROP_ONE_SLOT) {
            final PlayerData playerData = PlayerStorage.getInstance().get(player.getUniqueId());
            playerData.setShopSkinID(skinClicked.id());
            Messages.send(player, "shopkeepers.skin-change");
            return;
        }

        final Location loc = event.getWhoClicked().getLocation();
        final int entityId = new ShopKeeperManager().spawn(
            ((Player)event.getWhoClicked()),
            event.getWhoClicked().getWorld(),
            skinClicked.id(),
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            loc.getYaw());

        player.closeInventory();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> new ShopKeeperManager().deleteEntity(entityId, player), removeEntity);
        Messages.send(player, "shopkeepers-preview");
    }
}