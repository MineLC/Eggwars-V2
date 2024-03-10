package lc.eggwars.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.game.pregameitems.teamselector.InventorySelector;

public final class GameStorage {

    private static GameStorage storage;

    private final EggwarsPlugin plugin;
    private final PreGameCountdown.Data pregameData;
    private final Map<UUID, GameInProgress> playersInGame = new HashMap<>();

    GameStorage(EggwarsPlugin plugin, PreGameCountdown.Data data) {
        this.plugin = plugin;
        this.pregameData = data;
    }

    public void join(final World world, final GameInProgress map, final Player player) {
        playersInGame.put(player.getUniqueId(), map);
        map.getPlayers().add(player);

        if (map.getState() != GameState.NONE) {
            return;
        }

        map.setState(GameState.PREGAME);

        final InventorySelector.InventoryData selector = new InventorySelector().createBaseInventory(
            map.getMapData().getSpawns().keySet(),
            "Seleciona tu equipo",
            map.getMapData());

        final PreGameTemporaryData temporaryData = new PreGameTemporaryData(selector.inventory(), selector.teamsPerSlot());
        final PreGameCountdown waitToStartCountdown = new PreGameCountdown(
            pregameData,
            map.getPlayers(),
            () -> new GameManager().start(map),
            temporaryData
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, waitToStartCountdown, 0, 20).getTaskId();
        waitToStartCountdown.setId(id);
        map.setCountdown(waitToStartCountdown);
    }

    public void leave(final GameInProgress game, final Player player) {
        playersInGame.remove(player.getUniqueId());

        if (game.getCountdown() instanceof PreGameCountdown pregame) {
            pregame.getTemporaryData().leave(player);
            return;
        }

        new GameDeath(plugin).finalDeath(
            game,
            game.getTeamPerPlayer().get(player),
            player,
            true);
    }

    public GameInProgress getGame(UUID uuid) {
        return playersInGame.get(uuid);
    }

    public static GameStorage getStorage() {
        return storage;
    }

    final static void update(final GameStorage newStorage) {
        storage = newStorage;
    }
}