package lc.eggwars.game;

import lc.eggwars.teams.GameTeam;

public final class PlayerInGame {

    private GameTeam team;
    private final GameInProgress game;
    private long jumpPadTime = 0;

    PlayerInGame(GameInProgress game) {
        this.game = game;
    }

    public GameInProgress getGame() {
        return game;
    }

    public GameTeam getTeam() {
        return team;
    }

    public long getJumpPadTime() {
        return jumpPadTime;
    }

    void setTeam(final GameTeam team) {
        this.team = team;
    }

    public void setJumpPadTime(final long time) {
        this.jumpPadTime = time;
    }
}