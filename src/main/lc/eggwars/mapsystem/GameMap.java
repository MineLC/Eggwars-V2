package lc.eggwars.mapsystem;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.game.GameState;
import lc.eggwars.generators.SignGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;

import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GameMap {

    private final IntObjectHashMap<ClickableBlock> clickableBlocks;
    private final Map<BaseTeam, BlockLocation> spawns;
    private final SignGenerator[] generators;
    private final int borderSize;
    private final int id;

    private World world;
    private Set<BaseTeam> teamsWithEgg;
    private Map<Player, BaseTeam> playersPerTeam;
    private Set<Player> players;
    private GameState state = GameState.NONE;
    private int currentTaskId = -1;

    GameMap(IntObjectHashMap<ClickableBlock> clickableBlocks, SignGenerator[] generators, Map<BaseTeam, BlockLocation> spawns, int borderSize, int id) {
        this.clickableBlocks = clickableBlocks;
        this.generators = generators;
        this.spawns = spawns;
        this.borderSize = borderSize;
        this.id = id;
    }

    public void resetData() {
        this.players = new HashSet<>();
        this.playersPerTeam = new HashMap<>();
        this.teamsWithEgg = new HashSet<>();
    }

    public void setWorld(final World world) {
        this.world = world;
    }

    public void setState(final GameState state) {
        this.state = state;
    }

    public void setTaskId(int id) {
        this.currentTaskId = id;
    }

    public World getWorld() {
        return world;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Map<Player, BaseTeam> getPlayersPerTeam() {
        return playersPerTeam;
    }

    public Set<BaseTeam> getTeamsWithEggs() {
        return teamsWithEgg;
    }

    public GameState getState() {
        return state;
    }

    public int getTaskId() {
        return currentTaskId;
    }

    public BlockLocation getSpawn(final BaseTeam team) {
        return spawns.get(team);
    }

    public Map<BaseTeam, BlockLocation> getSpawns() {
        return spawns;
    }
    
    public SignGenerator[] getGenerators() {
        return generators;
    }

    public IntObjectHashMap<ClickableBlock> getClickableBlocks() {
        return clickableBlocks;
    }

    public int getBorderSize() {
        return borderSize;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof GameMap otherMap) ? otherMap.id == this.id : false;
    }
}