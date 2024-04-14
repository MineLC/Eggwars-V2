package lc.eggwars.game.threadtasks;

import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.events.EventStorage;

public final class EventTask {

    public void execute(final MapData map, final long time) {
        final GameInProgress game = map.getGameInProgress();
        if (game.getState() != GameState.IN_GAME || game.getNextEvent() == null) {
            return;
        }

        final long secondsTranscurred = (time - game.getStartedTime()) / 1000;

        if (secondsTranscurred >= game.getNextEvent().secondToStart()) {
            EventStorage.getStorage().loadEvent(game, game.getNextEvent());
            game.nextEvent();
            return;
        }

        final long secondsToStart = game.getNextEvent().secondToStart() - secondsTranscurred;

        if (secondsToStart < 10) {
            final String message = Messages.get("events.spam-message")
                .replace("%event%", game.getNextEvent().name())
                .replace("%time%", String.valueOf(secondsToStart));

            final Set<Player> players = game.getPlayers();
            Messages.sendNoGet(players, message);
            for (final Player player : players) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, -1);
            }
            return;
        }
        if (secondsTranscurred % 60 == 0) {
            Messages.sendNoGet(game.getPlayers(), Messages.get("events.time-remain")
            .replace("%time%", GameCountdown.parseTime(secondsToStart)));
        }
    }
}