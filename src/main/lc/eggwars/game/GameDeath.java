package lc.eggwars.game;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.CallbackGameCountdown;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.deaths.DeathStorage;
import lc.eggwars.others.levels.LevelStorage;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;

final class GameDeath {

    private final EggwarsPlugin plugin;

    public GameDeath(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void finalDeath(final GameInProgress game, final BaseTeam team, final Player player, final boolean quit) {
        game.getPlayersLiving().remove(player);

        if (quit) {
            game.getPlayers().remove(player);
            if (team != null) {
                game.getTeamPerPlayer().remove(player);
                game.getPlayersInTeam().get(team).remove(player);
            }
        }

        if (game.getPlayers().size() == 0) {
            new GameManager().stop(game);
            return;
        }

        LevelStorage.getStorage().onDeath(player, true);
        DeathStorage.getStorage().onDeath(game.getPlayers(), player, () -> {}, true);

        Messages.sendNoGet(game.getPlayers(), Messages.get("team.death").replace("%team%", team.getName()));
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());

        if (game.getTeamsWithEgg().size() > 1) {
            return;
        }

        final BaseTeam finalTeam = getLastTeamAlive(game);
        if (finalTeam == null) {
            return;
        }

        Messages.sendNoGet(game.getPlayers(), Messages.get("team.win").replace("%team%", finalTeam.getName()));        
        for (final Player livingPlayer : game.getPlayersLiving()) {
            livingPlayer.setGameMode(GameMode.SPECTATOR);
        }
        final Set<Player> winners = game.getPlayersInTeam().get(finalTeam);
        for (final Player winner : winners) {
            LevelStorage.getStorage().win(winner);
        }

        final GameCountdown endgameCountdown = new CallbackGameCountdown(() -> new GameManager().stop(game));

        int id = plugin.getServer().getScheduler().runTaskLater(
            plugin,
            endgameCountdown,
            plugin.getConfig().getInt("win-celebration-duration-in-seconds") * 20).getTaskId();

        endgameCountdown.setId(id);
        game.setCountdown(endgameCountdown);
    }

    private BaseTeam getLastTeamAlive(final GameInProgress game) {
        BaseTeam finalTeam = null;
        boolean anotherTeamLive = true;

        for (final Player livingPlayer : game.getPlayersLiving()) {
            if (finalTeam == null) {
                finalTeam = game.getTeamPerPlayer().get(livingPlayer);
                continue;
            }
            if (!finalTeam.equals(game.getTeamPerPlayer().get(livingPlayer))) {
                anotherTeamLive = true;
                break;
            }
        }
        return (anotherTeamLive) ? null : finalTeam;
    }
}