package lc.eggwars.others.selectgame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.mapsystem.MapStorage;
import lc.eggwars.utils.IntegerUtils;

public final class MapInventoryBuilder {

    private final List<String> gameLore;
    private final StateItem[] states;
    private final String soloMode, teamMode, inventoryName, timeLineLore, maxPlayersPerTeamLore;

    MapInventoryBuilder(StateItem[] states, String soloMode, String teamMode, List<String> gameLore, String inventoryName, String timeLineLore, String maxPlayersPerTeamLore) {
        this.states = states;
        this.soloMode = soloMode;
        this.teamMode = teamMode;
        this.gameLore = gameLore;
        this.inventoryName = inventoryName;
        this.timeLineLore = timeLineLore;
        this.maxPlayersPerTeamLore = maxPlayersPerTeamLore;
    }

    public Inventory build(final boolean soloMode) {
        final MapData[] maps = (soloMode) ? MapStorage.getStorage().getSoloMaps() : MapStorage.getStorage().getTeamMaps();
        final int rows = IntegerUtils.aproximate(maps.length, 9);
        final Inventory inventory = Bukkit.createInventory(
            new MapSelectorInventoryHolder(maps),
            rows * 9,
            inventoryName);

        int slot = 0;

        for (final MapData map : maps) {
            inventory.setItem(slot++, createMapItem(map));
        }
        return inventory;
    }

    private ItemStack createMapItem(final MapData map) {
        final GameInProgress game = map.getGameInProgress();
        final GameState gameState = (game == null) ? GameState.NONE : game.getState();
        final StateItem state = states[gameState.ordinal()];

        final ItemStack item = new ItemStack(state.material(), (game == null) ? 0 : game.getPlayers().size());
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(map.getName() + state.suffix());
        if (game == null) {
            meta.setLore(parseLore(0, 0, map.getMaxPlayers(), map.getMaxPersonsPerTeam(), gameState));
        } else {
            meta.setLore(parseLore(game.getPlayers().size(),  game.getStartedTime(), map.getMaxPlayers(), map.getMaxPersonsPerTeam(), gameState));
        }
        item.setItemMeta(meta);

        return item;
    }

    private List<String> parseLore(final int amountPlayers, final long startedTime, final int maxPlayers, final int maxTeamPlayer, final GameState state) {
        final List<String> newLore = new ArrayList<>(gameLore.size());
        final String playersFormat = amountPlayers + "/" + maxPlayers;
        final String mode = (maxTeamPlayer >= 2) ? teamMode : soloMode;

        for (final String line : gameLore) {
            if (line.isEmpty()) {
                newLore.add("");
                continue;
            }
            newLore.add(line
                .replace("%players%", playersFormat)
                .replace("%mode%", mode)
            );
        }

        if (state == GameState.IN_GAME || state == GameState.END_GAME) {
            newLore.add(timeLineLore.replace("%time%", GameCountdown.parseTime((System.currentTimeMillis() - startedTime) / 1000)));
        }
        if (maxTeamPlayer > 1) {
            newLore.add(maxPlayersPerTeamLore.replace("%max%", String.valueOf(maxTeamPlayer)));
        }
        return newLore;
    }
}