package lc.eggwars.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.PlayerInGame;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.pregame.PregameStorage;
import lc.eggwars.inventory.types.SelectMapInventory;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.others.specialitems.JumpPadItem;
import lc.eggwars.others.specialitems.PlatformItem;
import lc.eggwars.others.specialitems.TrackerItem;
import lc.eggwars.utils.ClickableBlock;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerInteractListener implements EventListener {

    private final SelectMapInventory selectMapInventory;

    public PlayerInteractListener(SelectMapInventory selectMapInventory) {
        this.selectMapInventory = selectMapInventory;
    }

    @ListenerData(
        event = PlayerInteractEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerInteractEvent event = (PlayerInteractEvent)defaultEvent;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.PHYSICAL
            || event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (event.getItem() != null && handleInteractWithItems(event)) {
            return;
        }
        event.setCancelled(false);
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getItem() != null) {
                handleWithSpawnItems(event.getPlayer(), event.getItem().getType());
            }
            return;
        }

        if (handleClickableBlocks(event)) {
            return;
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

    private boolean handleInteractWithItems(final PlayerInteractEvent event) {
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());
        final Material type = event.getItem().getType();

        if (playerInGame != null) {
            if (playerInGame.getGame().getState() == GameState.IN_GAME) {
                return handleSpecialItems(event, playerInGame, event.getPlayer(), event.getItem(), type);
            }
            if (playerInGame.getGame().getState() == GameState.PREGAME && PregameStorage.getStorage().selectTeam().item().getType() == type) {
                event.setCancelled(true);
                event.getPlayer().openInventory(((PreGameCountdown)playerInGame.getGame().getCountdown()).getTemporaryData().getTeamSelectorInventory());
                return true;
            }
        }

        handleWithSpawnItems(event.getPlayer(), type);
        return true;
    }

    private void handleWithSpawnItems(final Player player, final Material type) {
        if (type == SpawnStorage.getStorage().getShopItemMaterial()) {
            player.openInventory(SpawnStorage.getStorage().getShopInventory().getInventory());
            return;
        }
        if (type == SpawnStorage.getStorage().getGameItemMaterial()) {
            player.openInventory(selectMapInventory.getInventory());
            return;
        }
    }

    private boolean handleSpecialItems(final PlayerInteractEvent event, final PlayerInGame game, final Player player, final ItemStack item, final Material material) {
        switch (material) {
            case BEDROCK:
                new PlatformItem().handle(player, item);
                event.setCancelled(true);
                return true;
            case COMPASS:
                new TrackerItem().handle(player, game.getGame());
                return true;
            case GOLD_PLATE:
                new JumpPadItem().handleUp(player, item, game);
                event.setCancelled(true);
                return true;
            case IRON_PLATE:
                new JumpPadItem().handleDirectional(player, item, game);
                event.setCancelled(true);
                return true;
            default:
                event.setUseItemInHand(Result.ALLOW);
                return false;
        }
    }
}