package lc.eggwars.game.countdown;

public abstract class GameCountdown implements Runnable {

    private int id = -1;

    public static String parseTime(final long seconds) {
        if (seconds >= 60) {
            float minutes = roundDown2(seconds / 60.0F);
            if (minutes >= 60) {
                float hours = roundDown2(minutes / 60.0D);
                return hours + (hours > 1 ? " horas" : " hora");
            }
            return minutes + (minutes > 1 ? " minutos" : " minuto");
        }
        return seconds + (seconds > 1 ? " segundos" : " segundo");
    }

    private static float roundDown2(double d) {
        return (float) (((long)(d * 1e2)) / 1e2);
    }
    
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}