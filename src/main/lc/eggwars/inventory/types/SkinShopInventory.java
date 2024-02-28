package lc.eggwars.inventory.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData;
import lc.eggwars.inventory.CustomInventory;
import lc.eggwars.messages.Messages;

import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public final class SkinShopInventory implements CustomInventory {

    private final int removeEntityDelay;

    public SkinShopInventory() {
        this.removeEntityDelay = EggwarsPlugin.getInstance().getConfig().getInt("shopkeepers.preview-seconds-duration") * 20;
    }

    @Override
    public void handle(InventoryClickEvent event) {
        final ShopkeepersData data = ShopKeepersStorage.getStorage().data();
        final ShopkeepersData.Skin skinClicked = data.items().get(event.getSlot());

        event.setCancelled(true);

        if (skinClicked == null) {
            return;
        }

        final Player player = (Player)event.getWhoClicked();

        if (event.getAction() != InventoryAction.DROP_ONE_SLOT) {
            final StatsEggWars stats = Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();
            if (stats.getShopKeeperSkinList().contains(String.valueOf(skinClicked.id()))) {
                stats.setShopKeeperSkinSelected(skinClicked.id());
                Messages.send(player, "shopkeepers.skin-change");
                return;
            }
            if (stats.getLCoins() < skinClicked.cost()) {
                Messages.send(player, "shopkeepers.no-money");
                return;
            }
            stats.setLCoins(stats.getLCoins() - skinClicked.cost());
            stats.setShopKeeperSkinSelected(skinClicked.id());
            stats.getShopKeeperSkinList().add(String.valueOf(skinClicked.id()));
            Messages.send(player, "shopkeepers.skin-change");
            return;
        }

        final Location loc = event.getWhoClicked().getLocation();
        final int entityId = new ShopKeeperManager().spawn(
            ((Player)event.getWhoClicked()),
            event.getWhoClicked().getWorld(),
            skinClicked.id(),
            Integer.MAX_VALUE - 1,
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            loc.getYaw());

        player.closeInventory();
        EggwarsPlugin.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(
            EggwarsPlugin.getInstance(),
            () -> new ShopKeeperManager().deleteEntity(entityId, player), removeEntityDelay);
        Messages.send(player, "shopkeepers-preview");
    }
}