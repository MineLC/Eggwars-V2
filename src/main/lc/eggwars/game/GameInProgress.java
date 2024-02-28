package lc.eggwars.game;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.mapsystem.MapData;
import lc.eggwars.teams.BaseTeam;

import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GameInProgress {

    private final MapData data;
    private final World world;

    private boolean generatorsNeedUpdate = true;

    private Set<BaseTeam> teamsWithEgg = new HashSet<>();

    private Map<Player, BaseTeam> teamPerPlayer = new HashMap<>();
    private Map<BaseTeam, Set<Player>> playersInTeams = new HashMap<>();

    private Set<Player> players = new HashSet<>();
    private Set<Player> playersLiving = new HashSet<>();

    private GameState state = GameState.NONE;
    private GameCountdown getCountdown;

    public GameInProgress(MapData data, World world) {
        this.data = data;
        this.world = world;
    }

    public void setState(final GameState state) {
        this.state = state;
    }

    public void setCountdown(GameCountdown countdown) {
        this.getCountdown = countdown;
    }

    public MapData getMapData() {
        return data;
    }

    public World getWorld() {
        return world;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Map<Player, BaseTeam> getTeamPerPlayer() {
        return teamPerPlayer;
    }

    public Set<BaseTeam> getTeamsWithEgg() {
        return teamsWithEgg;
    }

    public GameState getState() {
        return state;
    }

    public GameCountdown getCountdown() {
        return getCountdown;
    }

    public boolean generatorsNeedUpdate() {
        return generatorsNeedUpdate;
    }

    public void setGeneratorsNeedUpdate(boolean update) {
        this.generatorsNeedUpdate = update;
    }

    public Map<BaseTeam, Set<Player>> getPlayersInTeam() {
        return playersInTeams;
    }

    public Set<Player> getPlayersLiving() {
        return playersLiving;
    }
}