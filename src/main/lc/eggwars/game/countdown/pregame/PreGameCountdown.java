package lc.eggwars.game.countdown.pregame;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lc.eggwars.game.GameInProgress;
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
    private final GameInProgress game;
    private final Data data;
    private final CountdownCallback complete;
    private PreGameTemporaryData temporary;
    private boolean alreadyFastStart = false;

    public PreGameCountdown(Data data, GameInProgress game, CountdownCallback completeCountdown, PreGameTemporaryData temporary) {
        this.data = data;
        this.waitingCountdown = data.waitingTime;
        this.countdown = data.waitingTime;
        this.players = game.getPlayers();
        this.game = game;
        this.complete = completeCountdown;
        this.temporary = temporary;
    }

    @Override
    public void run() {
        if (countdown % data.sidebarUpdateTime == 0) {
            final EggwarsSidebar sidebar = SidebarStorage.getStorage().getSidebar(SidebarType.PREGAME);
            sidebar.send(players);   
        }

        if (players.size() < data.minPlayers) {
            if (waitingCountdown % data.waitingTime == 0) {
                Messages.send(players, "pregame.waiting-players");
            }
            countdown = data.waitingTime;
            --waitingCountdown;
            if (waitingCountdown <= 0) {
                waitingCountdown = data.waitingTime;
            }
            return;
        }
        if (!alreadyFastStart && players.size() == game.getMapData().getMaxPlayers()) {
            countdown = data.messageTime;
            alreadyFastStart = true;
        }

        players.forEach((player) -> player.setLevel(countdown));

        if (countdown <= 0) {
            temporary = null;
            players.forEach((player) -> player.setLevel(0));

            complete.execute();
            Messages.send(players, "pregame.start-game");
            Bukkit.getScheduler().cancelTask(getId());
            return;
        }

        // Send the message every x seconds
        if (countdown % data.messageTime == 0) {
            Messages.sendNoGet(
                players,
                Messages.get("pregame.start-in").replace("%time%", parseTime(countdown))
            );
            countdown--;
            return;
        }
        if (countdown <= data.spamMessage) {
            Messages.sendNoGet(
                players,
                Messages.get("pregame.start-in").replace("%time%", parseTime(countdown))
            );
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

    public Data getData() {
        return data;
    }

    public PreGameTemporaryData getTemporaryData() {
        return temporary;
    }

    public static record Data(
        int sidebarUpdateTime,
        int waitingTime,
        int messageTime,
        int secondsToMakeSound, 
        int spamMessage,
        int minPlayers) {
    }
}