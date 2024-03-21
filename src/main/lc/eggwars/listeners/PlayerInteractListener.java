package lc.eggwars.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.pregame.PregameStorage;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.others.specialitems.PlatformItem;
import lc.eggwars.others.specialitems.TrackerItem;
import lc.eggwars.utils.ClickableBlock;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerInteractListener implements EventListener {

    @ListenerData(
        event = PlayerInteractEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerInteractEvent event = (PlayerInteractEvent)defaultEvent;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.PHYSICAL) {
            return;
        }

        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getItem() == null) {
                return;
            }
            final Inventory inventory = SpawnStorage.getStorage().items().get(event.getItem().getType());
            if (inventory != null) {
                event.getPlayer().openInventory(inventory);
            }
            return;
        }

        if (handleClickableBlocks(event)) {
            return;
        }

        if (event.getItem() != null) {
            handleInteractWithItems(event);
        }
    }

    private boolean handleClickableBlocks(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) {
            return false;
        }
        final Location location = block.getLocation();
        final ClickableBlock clickableBlock = MapStorage.getStorage().getClickableBlock(location.getWorld(), location);

        if (clickableBlock == null) {
            return false;
        }
        event.setCancelled(true);

        if (!clickableBlock.supportLeftClick() && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return true;
        }

        clickableBlock.onClick(event.getPlayer(), event.getAction());
        return true;
    }

    private void handleInteractWithItems(final PlayerInteractEvent event) {
        final GameInProgress game = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());
        final Material type = event.getItem().getType();

        if (game != null) {
            if (game.getState() == GameState.IN_GAME) {
                handleSpecialItems(game, event.getPlayer(), type);
                return;
            }
            if (!(game.getCountdown() instanceof PreGameCountdown pregame)) {
                return;
            }
            if (PregameStorage.getStorage().selectTeam().item().getType() == type) {
                event.setCancelled(true);
                event.getPlayer().openInventory(pregame.getTemporaryData().getTeamSelectorInventory());
                return;
            }
        }

        final Inventory inventory = SpawnStorage.getStorage().items().get(type);
        if (inventory != null) {
            event.setCancelled(true);
            event.getPlayer().openInventory(inventory);
        }
    }

    private void handleSpecialItems(final GameInProgress game, final Player player, final Material material) {
        switch (material) {
            case BEDROCK:
                new PlatformItem().handle(player);
                break;
            case COMPASS:
                new TrackerItem().handle(player, game);
                break;
            default:
                return;
        }
    }
}