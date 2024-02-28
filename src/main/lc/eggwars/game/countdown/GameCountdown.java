package lc.eggwars.game.countdown;

public abstract class GameCountdown implements Runnable {

    private int id = -1;

    protected String parseTime(final int seconds) {
        if (seconds >= 60) {
            int minutes = seconds / 60;
            return minutes + " minuto" + (minutes > 1 ? 's' : ' ');
        }
        return seconds + " segundo" + (seconds > 1 ? 's' : ' ');
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}