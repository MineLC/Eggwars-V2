package lc.eggwars.others.deaths;

import java.util.Collection;

import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.CountdownCallback;
import lc.eggwars.messages.Messages;

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

    public void onDeath(final Collection<Player> playersToSendMessage, final Player player, final CountdownCallback onCompleteCinematic, final boolean finalKill) {
        if (finalKill) {
            final String message = createMessage(player, true);
            if (message != null) {
                Messages.sendNoGet(playersToSendMessage, message);
            }
            onCompleteCinematic.execute();
            return;
        }

        final DeathCinematic cinematic = new DeathCinematic(onCompleteCinematic, respawnTitle, respawnSubtitle, respawnWaitTime, player);
        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> cinematic.run(), 0, 20).getTaskId();
        cinematic.setId(id);
        final String message = createMessage(player, false);

        if (message != null) {
            Messages.sendNoGet(playersToSendMessage, message);
        }
    }

    private String createMessage(final Player player, boolean finalKill) {
        String deathMessage = (player.getLastDamageCause() == null)
            ? fallbackDeathMessage
            : deathMessages[player.getLastDamageCause().getCause().ordinal()];

        if (deathMessage == null) {
            deathMessage = fallbackDeathMessage;
        }

        String finalMessage = (finalKill) ? finalKillPrefix : "";

        finalMessage = finalMessage + deathMessage.replace("%v%", player.getName());
        if (player.getKiller() != null) {
            finalMessage = finalMessage.replace("%d%", player.getKiller().getName()) + suffixIfPlayerKill;
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
