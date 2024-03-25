package lc.eggwars.others.events;

import org.bukkit.Bukkit;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.countdown.GameCountdown;

final class EventCountdown extends GameCountdown {
    private final GameInProgress game;
    private int duration;
    private Runnable task;

    public EventCountdown(final GameInProgress game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (duration-- <= 0) {
            Bukkit.getScheduler().cancelTask(getId());
            game.setCountdown(null);
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