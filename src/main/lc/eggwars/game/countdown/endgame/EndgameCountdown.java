package lc.eggwars.game.countdown.endgame;

import lc.eggwars.game.countdown.CountdownCallback;
import lc.eggwars.game.countdown.GameCountdown;

public class EndgameCountdown extends GameCountdown  {

    private final CountdownCallback callback;

    public EndgameCountdown(CountdownCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        callback.execute();
    }
}