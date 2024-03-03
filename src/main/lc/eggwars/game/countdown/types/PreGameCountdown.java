package lc.eggwars.game.countdown.types;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lc.eggwars.game.countdown.CountdownCallback;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.EggwarsSidebar;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;

import java.util.Set;

public class PreGameCountdown extends GameCountdown {

    private int countdown = 0;
    private int waitingCountdown = 0;

    private final Set<Player> players;
    private final Data data;
    private final CountdownCallback complete;

    public PreGameCountdown(Data data, Set<Player> players, CountdownCallback completeCountdown) {
        this.data = data;
        this.waitingCountdown = data.waitingTime;
        this.countdown = data.waitingTime;
        this.players = players;
        this.complete = completeCountdown;
    }

    @Override
    public void run() {
        final EggwarsSidebar sidebar = SidebarStorage.getStorage().getSidebar(SidebarType.PREGAME);
        sidebar.send(players);

        if (players.size() < data.minPlayers) {
            if (waitingCountdown % data.waitingTime == 0) {
                Messages.sendNoGet(players, "Esperando por más jugadores");
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
            Messages.sendNoGet(players, "Iniciando el juego");
            Bukkit.getScheduler().cancelTask(getId());
            return;
        }

        // Send the message every x seconds
        if (countdown % data.messageTime == 0) {
            Messages.sendNoGet(players, "Iniciando el juego en: " + parseTime(countdown));
            countdown--;
            return;
        }

        if (countdown <= data.spamMessage) {
            Messages.sendNoGet(players, "Iniciando el juego en: " + parseTime(countdown));
        }

        if (countdown <= data.secondsToMakeSound) {
            for (final Player player : players) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, -1);
            }
        }

        countdown--;
    }

    public String getCountdown() {
        return parseTime(countdown);
    }

    public static record Data(
        int waitingTime,
        int messageTime,
        int secondsToMakeSound, 
        int spamMessage,
        int minPlayers) {
    }
}