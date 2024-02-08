package lc.eggwars.game.countdown.types;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lc.eggwars.game.countdown.CountdownCallback;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.utils.Chat;

import java.util.Set;

public class PreGameCountdown implements GameCountdown {

    private int countdown = 0;
    private int waitingCountdown = 0;
    private final Set<Player> players;
    private final Data data;
    private final CountdownCallback complete, cancel;

    private int id;

    public PreGameCountdown(Data data, Set<Player> players, CountdownCallback completeCountdown, CountdownCallback cancelGame) {
        this.data = data;
        this.waitingCountdown = data.waitingTime;
        this.countdown = data.waitingTime;
        this.players = players;
        this.complete = completeCountdown;
        this.cancel = cancelGame;
    }

    @Override
    public void run() {
        if (players.size() == 0) {
            cancel.execute();
            Bukkit.getScheduler().cancelTask(id);
            return;
        }

        if (players.size() < data.minPlayers) {
            if (waitingCountdown % data.waitingTime == 0) {
                Chat.send(data.messageWaitingPlayers, players);
            }
            countdown = data.waitingTime;
            --waitingCountdown;
            if (waitingCountdown <= 0) {
                waitingCountdown = data.waitingTime;
            }
            return;
        }

        if (countdown <= 0) {
            complete.execute();
            Chat.send(data.messageStartGame, players);
            Bukkit.getScheduler().cancelTask(id);
            return;
        }

        // Send the message every x seconds
        if (countdown % data.messageTime == 0) {
            Chat.send(data.messageGameStartIn.replace("%time%", parseTime(countdown)), players);
            countdown--;
            return;
        }

        if (countdown <= data.spamMessage) {
            Chat.send(data.messageGameStartIn.replace("%time%", parseTime(countdown)), players);
        }

        if (countdown <= data.secondsToMakeSound) {
            for (final Player player : players) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, -1);
            }
        }

        countdown--;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static record Data(
        int waitingTime,
        int messageTime,
        int secondsToMakeSound, 
        int spamMessage,
        int minPlayers,
        String messageWaitingPlayers,
        String messageStartGame,
        String messageGameStartIn) {
    }
}