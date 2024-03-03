package lc.eggwars.game.countdown.types;

import lc.eggwars.game.countdown.CountdownCallback;
import lc.eggwars.game.countdown.GameCountdown;

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