package lc.eggwars.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.endgame.EndgameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameCountdown;
import lc.eggwars.game.countdown.pregame.PreGameTemporaryData;
import lc.eggwars.game.pregame.teamselector.InventorySelector;
import lc.eggwars.teams.GameTeam;

public final class GameStorage {

    private static GameStorage storage;

    private final EggwarsPlugin plugin;
    private final PreGameCountdown.Data pregameData;
    private final Map<UUID, PlayerInGame> playersInGame = new HashMap<>();

    GameStorage(EggwarsPlugin plugin, PreGameCountdown.Data data) {
        this.plugin = plugin;
        this.pregameData = data;
    }

    public void join(final String world, final GameInProgress game, final Player player) {
        playersInGame.put(player.getUniqueId(), new PlayerInGame(game));
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
            () -> new GameStartAndStop().start(plugin, game, world),
            temporaryData
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, waitToStartCountdown, 0, 20).getTaskId();
        waitToStartCountdown.setId(id);

        game.setCountdown(waitToStartCountdown);
        game.setState(GameState.PREGAME);
    }

    public void stop(final GameInProgress game) {
        new GameStartAndStop().stop(game);
    }

    public void leave(final GameInProgress game, final Player player, final boolean leaveFromGame) {
        final PlayerInGame playerInGame = playersInGame.remove(player.getUniqueId());
    
        if (game.getCountdown() instanceof EndgameCountdown) {
            game.getPlayers().remove(player);
            if (game.getPlayers().isEmpty()) {
                stop(game);
                return;
            }
            final GameTeam team = game.getTeamPerPlayer().get(player);
            if (team != null) {
                team.remove(player);
            }
            game.getTeamPerPlayer().remove(player);
            return;
        }
        if (game.getCountdown() instanceof PreGameCountdown pregame) {
            game.getPlayers().remove(player);
            pregame.getTemporaryData().leave(player, game);
            game.getTeamPerPlayer().remove(player);
            if (game.getPlayers().isEmpty()) {
                game.getMapData().setGame(null);
                plugin.getServer().getScheduler().cancelTask(game.getCountdown().getId());
            }
            return;
        }
        if (player.getGameMode() != GameMode.SPECTATOR || playerInGame.getInDeathCinematic()) {
            new GameDeath(plugin).death(
                playerInGame,
                game.getTeamPerPlayer().get(player),
                player,
                leaveFromGame,
                true);
        }
    }

    public void remove(final UUID uuid) {
        playersInGame.remove(uuid);
    }

    public PlayerInGame getPlayerInGame(final UUID uuid) {
        return playersInGame.get(uuid);
    }

    public GameInProgress getGame(UUID uuid) {
        final PlayerInGame playerInGame = playersInGame.get(uuid);
        return (playerInGame == null) ? null : playerInGame.getGame();
    }

    public static GameStorage getStorage() {
        return storage;
    }

    final static void update(final GameStorage newStorage) {
        storage = newStorage;
    }
}