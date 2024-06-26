package lc.eggwars.inventory.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData;
import lc.eggwars.messages.Messages;
import lc.eggwars.utils.EntityLocation;

public final class SkinShopInventory {

    private final EggwarsPlugin plugin;
    private final int removeEntityDelay;

    public SkinShopInventory(EggwarsPlugin plugin) {
        this.plugin = plugin;
        this.removeEntityDelay = plugin.getConfig().getInt("shopkeepers.preview-seconds-duration") * 20;
    }

    public void handle(InventoryClickEvent event) {
        final ShopkeepersData skinData = ShopKeepersStorage.getStorage().data();
        final ShopkeepersData.Skin skinClicked = skinData.items().get(event.getSlot());

        event.setCancelled(true);

        if (skinClicked == null) {
            return;
        }

        final Player player = (Player)event.getWhoClicked();

        if (event.getAction() != InventoryAction.DROP_ONE_SLOT) {
            final PlayerData data = PlayerDataStorage.getStorage().get(event.getWhoClicked().getUniqueId());
            if (data.skins.contains(skinClicked.id())) {
                data.skinSelected = skinClicked.id();
                Messages.send(player, "shopkeepers.skin-change");
                return;
            }
            if (data.coins < skinClicked.cost()) {
                Messages.send(player, "shopkeepers.no-money");
                return;
            }
            if (skinClicked.permission() != null && !player.hasPermission(skinClicked.permission())) {
                Messages.send(player, "shopkeepers.no-perms");
                return;
            }
            data.coins -= skinClicked.cost();
            data.skinSelected = skinClicked.id();
            data.skins.add(skinClicked.id());
            Messages.send(player, "shopkeepers.skin-change");
            return;
        }

        final Location loc = event.getWhoClicked().getLocation();
        final int entityId = new ShopKeeperManager().spawn(
            ((Player)event.getWhoClicked()),
            event.getWhoClicked().getWorld(),
            skinClicked.id(),
            Integer.MAX_VALUE - 1,
            new EntityLocation(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ(), loc.getYaw(), loc.getPitch()));

        player.closeInventory();
        plugin.getServer().getScheduler().runTaskLater(
            plugin,
            () -> new ShopKeeperManager().deleteEntity(entityId, player), removeEntityDelay);
        Messages.send(player, "shopkeepers-preview");
    }
}