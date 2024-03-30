package lc.eggwars.game;

import lc.eggwars.teams.GameTeam;

public final class PlayerInGame {

    private GameTeam team;
    private final GameInProgress game;

    private boolean deadCinematic = false;
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

    public boolean getInDeathCinematic() {
        return deadCinematic;
    }

    void setTeam(final GameTeam team) {
        this.team = team;
    }

    public void setJumpPadTime(final long time) {
        this.jumpPadTime = time;
    }

    public void setDeathCinematic(final boolean value) {
        this.deadCinematic = value;
    }
}