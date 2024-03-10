package lc.eggwars.game.countdown;

public final class CallbackGameCountdown extends GameCountdown {

    private final CountdownCallback callback;

    public CallbackGameCountdown(CountdownCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        callback.execute();
    }
}