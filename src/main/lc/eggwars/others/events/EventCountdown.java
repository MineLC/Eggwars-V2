package lc.eggwars.others.events;

import org.bukkit.Bukkit;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.messages.Messages;

final class EventCountdown extends GameCountdown {
    private final GameInProgress game;
    private final String eventName;
    private int duration;
    private Runnable task;

    public EventCountdown(final GameInProgress game, final String eventName) {
        this.game = game;
        this.eventName = eventName;
    }

    @Override
    public void run() {
        if (duration-- <= 0) {
            Bukkit.getScheduler().cancelTask(getId());
            game.setCountdown(null);
            Messages.sendNoGet(game.getPlayers(), Messages.get("events.end").replace("%event%", eventName));
            return;
        }

        if (task != null) {
            task.run();
        }
    }

    void setTask(final Runnable task) {
        this.task = task;
    }

    void setDuration(final int duration) {
        this.duration = duration;
    }
}