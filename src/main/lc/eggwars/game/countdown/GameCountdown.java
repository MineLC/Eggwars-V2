package lc.eggwars.game.countdown;

public interface GameCountdown extends Runnable {

    public default String parseTime(final int seconds) {
        if (seconds >= 60) {
            int minutes = seconds / 60;
            return minutes + " minuto" + (minutes > 1 ? 's' : ' ');
        }
        return seconds + " segundo" + (seconds > 1 ? 's' : ' ');
    }
}