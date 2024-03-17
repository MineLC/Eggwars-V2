package lc.eggwars.game;

import java.util.Set;

import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.endgame.EndgameCountdown;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.deaths.DeathStorage;
import lc.eggwars.others.levels.LevelStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;

public final class GameDeath {

    private final EggwarsPlugin plugin;

    public GameDeath(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void death(final GameInProgress game, final GameTeam gameTeam, final Player player, final boolean leaveFromGame, final boolean finalKill) {
        final BaseTeam team = gameTeam.getBase();

        if (leaveFromGame) {
            game.getPlayers().remove(player);
            game.getTeamPerPlayer().remove(player);
            gameTeam.remove(player);

            if (game.getPlayers().isEmpty()) {
                new GameStartAndStop().stop(game);
                return;
            }
        } else {
            gameTeam.addPlayerDeath();
        }

        LevelStorage.getStorage().onDeath(player, finalKill);
        DeathStorage.getStorage().onDeath(game.getPlayers(), player, () -> {}, finalKill);

        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());

        if (gameTeam.getPlayerDeaths() != gameTeam.getPlayers().size()) {
            return;
        }

        Messages.sendNoGet(game.getPlayers(), Messages.get("team.death").replace("%team%", team.getName()));
        final GameTeam finalTeam = getLastTeamAlive(game);
       
        if (finalTeam == null) {
            return;
        }
        
        final Set<Player> winners = finalTeam.getPlayers();
        for (final Player winner : winners) {
            LevelStorage.getStorage().win(winner);
        }

        final EndgameCountdown endgameCountdown = new EndgameCountdown(game);

        Messages.sendNoGet(game.getPlayers(), Messages.get("team.win")
            .replace("%team%", finalTeam.getBase().getName())
            .replace("%time%", endgameCountdown.parseTime((System.currentTimeMillis() - game.getStartedTime()) / 1000)));

        game.setState(GameState.END_GAME);

        int id = plugin.getServer().getScheduler().runTaskLater(
            plugin,
            endgameCountdown,
            plugin.getConfig().getInt("win-celebration-duration-in-seconds") * 20).getTaskId();

        endgameCountdown.setId(id);
        game.setCountdown(endgameCountdown);
    }

    private GameTeam getLastTeamAlive(final GameInProgress game) {
        GameTeam lastTeam = null;
        final Set<GameTeam> teams = game.getTeams();

        for (final GameTeam team : teams) {
            if (team.getPlayers().size() == team.getPlayerDeaths()) {
                continue;
            }
            if (lastTeam != null) {
                return null;
            }
            lastTeam = team;
        }

        return lastTeam;
    }
}