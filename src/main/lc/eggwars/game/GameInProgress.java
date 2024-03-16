package lc.eggwars.game;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GameInProgress {

    private final MapData data;
    private final World world;

    private final Map<Player, GameTeam> teamPerPlayer = new HashMap<>();
    private final Map<BaseTeam, GameTeam> teamPerBase = new HashMap<>();
    private final Set<GameTeam> teams = new HashSet<>();
    private final Set<Player> players = new HashSet<>();

    private GameState state = GameState.NONE;
    private GameCountdown countdown;
    private long startTime;

    public GameInProgress(MapData data, World world) {
        this.data = data;
        this.world = world;
    }

    public void setState(final GameState state) {
        this.state = state;
    }

    public void startTime() {
        this.startTime = System.currentTimeMillis();
    }

    public void setCountdown(GameCountdown countdown) {
        this.countdown = countdown;
    }

    public MapData getMapData() {
        return data;
    }

    public World getWorld() {
        return world;
    }

    public long getStartedTime() {
        return startTime;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Map<Player, GameTeam> getTeamPerPlayer() {
        return teamPerPlayer;
    }

    public Map<BaseTeam, GameTeam> getTeamPerBase() {
        return teamPerBase;
    }

    public GameState getState() {
        return state;
    }

    public GameCountdown getCountdown() {
        return countdown;
    }

    public Set<GameTeam> getTeams() {
        return teams;
    }

    public boolean playerIsDead(final Player player) {
        return player.getGameMode() == GameMode.SPECTATOR;
    }
}