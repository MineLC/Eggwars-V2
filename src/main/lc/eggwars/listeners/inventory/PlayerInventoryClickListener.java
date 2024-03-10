package lc.eggwars.listeners.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.inventory.CustomInventory;
import lc.eggwars.inventory.types.SkinShopInventory;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.teams.BaseTeam;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.utils.InventoryUtils;

public class PlayerInventoryClickListener implements EventListener {

    private final CustomInventory shopSkinInventory;

    public PlayerInventoryClickListener(EggwarsPlugin plugin) {
        this.shopSkinInventory = new SkinShopInventory(plugin);
    }

    @ListenerData(
        event = InventoryClickEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(final Event defaultEvent) {
        final InventoryClickEvent event = (InventoryClickEvent)defaultEvent;

        if (event.getClickedInventory() == null) {
            return;
        }

        final int inventory = InventoryUtils.getId(event.getClickedInventory());
        if (inventory == -1) {
            return;
        }

        event.setCancelled(true);

        if (!SpawnStorage.getStorage().isInSpawn(event.getWhoClicked())) {
            final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getWhoClicked().getUniqueId());

            if (gameInProgress == null || gameInProgress.getState() != GameState.PREGAME) {
                return;
            }
            final PreGameTemporaryData temporaryData = ((PreGameCountdown)gameInProgress.getCountdown()).getTemporaryData();

            if (InventoryUtils.getId(temporaryData.getTeamSelectorInventory()) == inventory) {
                tryJoinToTeam(temporaryData, (Player)event.getWhoClicked(), gameInProgress, event.getSlot());
                return;
            }
        }
 
        if (inventory == InventoryUtils.getId(SpawnStorage.getStorage().shopInventory().getInventory())) {
            SpawnStorage.getStorage().shopInventory().handle(event);
            return;
        }

        if (inventory == InventoryUtils.getId(ShopKeepersStorage.getStorage().data().skinShopInventory())) {
            shopSkinInventory.handle(event);
            return;
        }

        if (inventory == InventoryUtils.getId(KitStorage.getStorage().inventory().getInventory())) {
            KitStorage.getStorage().inventory().handle(event);
        }
    }

    private void tryJoinToTeam(final PreGameTemporaryData data, final Player player, final GameInProgress game, final int clickedSlot) {
        final BaseTeam team = data.getTeam(clickedSlot);
        if (team == null) {
            return;
        }
        data.joinToTeam(player, team);
        player.sendMessage(Messages.get("team.join").replace("%team%", team.getKey()));
    }
}