package lc.eggwars.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.endgame.EndgameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.game.pregameitems.teamselector.InventorySelector;
import lc.eggwars.teams.BaseTeam;

public final class GameStorage {

    private static GameStorage storage;

    private final EggwarsPlugin plugin;
    private final PreGameCountdown.Data pregameData;
    private final Map<UUID, GameInProgress> playersInGame = new HashMap<>();

    GameStorage(EggwarsPlugin plugin, PreGameCountdown.Data data) {
        this.plugin = plugin;
        this.pregameData = data;
    }

    public void join(final World world, final GameInProgress game, final Player player) {
        playersInGame.put(player.getUniqueId(), game);
        game.getPlayers().add(player);

        if (game.getState() != GameState.NONE) {
            return;
        }

        final InventorySelector.InventoryData selector = new InventorySelector().createBaseInventory(
            game.getMapData().getSpawns().keySet(),
            "Seleciona tu equipo",
            game.getMapData());

        final PreGameTemporaryData temporaryData = new PreGameTemporaryData(selector.inventory(), selector.teamsPerSlot());
        final PreGameCountdown waitToStartCountdown = new PreGameCountdown(
            pregameData,
            game.getPlayers(),
            () -> {
                new GameManager().start(game);
                game.setCountdown(null);
            },
            temporaryData
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, waitToStartCountdown, 0, 20).getTaskId();
        waitToStartCountdown.setId(id);

        game.setCountdown(waitToStartCountdown);
        game.setState(GameState.PREGAME);
    }

    public void leave(final GameInProgress game, final Player player, final boolean leaveFromGame) {
        playersInGame.remove(player.getUniqueId());

        if (game.getCountdown() instanceof EndgameCountdown) {
            final BaseTeam team = game.getTeamPerPlayer().get(player);
            team.getTeam().removePlayer(player);

            if (game.getPlayers().isEmpty()) {
                new GameManager().stop(game);
                return;
            }
            game.getPlayers().remove(player);
            game.getPlayersLiving().remove(player);
            game.getPlayersInTeam().get(team).remove(player);
            game.getTeamPerPlayer().remove(player);
            return;
        }

        if (game.getCountdown() instanceof PreGameCountdown pregame) {
            game.getPlayers().remove(player);
            pregame.getTemporaryData().leave(player, game);
            if (game.getPlayers().isEmpty()) {
                new GameManager().stop(game);
            }
            return;
        }

        new GameDeath(plugin).death(
            game,
            game.getTeamPerPlayer().get(player),
            player,
            leaveFromGame);
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