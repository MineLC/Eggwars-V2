package lc.eggwars.others.deaths;

import java.util.Collection;

import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.PlayerInGame;
import lc.eggwars.game.countdown.CountdownCallback;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.GameTeam;
import lc.eggwars.teams.TeamStorage;

public final class DeathStorage {
    private static DeathStorage storage;

    private final String[] deathMessages;
    private final String fallbackDeathMessage;
    private final String finalKillPrefix, suffixIfPlayerKill;

    private final String respawnTitle, respawnSubtitle;
    private final int respawnWaitTime;

    private final EggwarsPlugin plugin;

    DeathStorage(EggwarsPlugin plugin, String[] deathMessages, String fallbackDeathMessage, String finalKillPrefix, String suffixIfPlayerKill, String title, String subtitle, int waitingTime) {
        this.plugin = plugin;
        this.deathMessages = deathMessages;
        this.fallbackDeathMessage = fallbackDeathMessage;
        this.finalKillPrefix = finalKillPrefix;
        this.suffixIfPlayerKill = suffixIfPlayerKill;
        this.respawnTitle = title;
        this.respawnSubtitle = subtitle;
        this.respawnWaitTime = waitingTime;
    }

    public void onDeath(final PlayerInGame game, final Collection<Player> playersToSendMessage, final Player player, final CountdownCallback onCompleteCinematic, final boolean finalKill) {
        if (finalKill) {
            final String message = createMessage(game.getGame(), player, true);
            if (message != null) {
                Messages.sendNoGet(playersToSendMessage, message);
            }
            onCompleteCinematic.execute();
            game.setDeathCinematic(false);
            return;
        }
        game.setDeathCinematic(true);
        final DeathCinematic cinematic = new DeathCinematic(onCompleteCinematic, respawnTitle, respawnSubtitle, respawnWaitTime, player);
        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            cinematic.run();
            game.setDeathCinematic(false);
        }, 0, 20).getTaskId();
        cinematic.setId(id);
        final String message = createMessage(game.getGame(), player, false);

        if (message != null) {
            Messages.sendNoGet(playersToSendMessage, message);
        }
    }

    private String createMessage(final GameInProgress game, final Player player, boolean finalKill) {
        String deathMessage = (player.getLastDamageCause() == null)
            ? fallbackDeathMessage
            : deathMessages[player.getLastDamageCause().getCause().ordinal()];
        
        if (deathMessage == null) {
            deathMessage = fallbackDeathMessage;
        }

        String finalMessage = (finalKill) ? finalKillPrefix : "";
        GameTeam team = game.getTeamPerPlayer().get(player);

        finalMessage = finalMessage + deathMessage.replace("%v%", TeamStorage.getStorage().tryAddTeamPrefix(team, player));

        if (player.getKiller() != null) {
            team = game.getTeamPerPlayer().get(player.getKiller());
            finalMessage = finalMessage + suffixIfPlayerKill.replace("%d%", TeamStorage.getStorage().tryAddTeamPrefix(team, player.getKiller()));
        }
        return finalMessage;
    }

    static void update(DeathStorage newStorage) {
        storage = newStorage;
    }

    public static DeathStorage getStorage() {
        return storage;
    }
}
