package lc.eggwars.game;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.EntityLocation;

import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GameMap {

    private final IntObjectHashMap<ClickableBlock> clickableBlocks;
    private final Map<BaseTeam, BlockLocation> spawns;
    private final Map<BaseTeam, BlockLocation> eggs;
    private final ClickableSignGenerator[] generators;
    private final EntityLocation[] shopSpawns;

    private final int[] shopkeepersID;
    private final int maxPersonsPerTeam;
    private final int borderSize;
    private final int id;

    private boolean generatorsNeedUpdate = true;

    private World world;
    private Set<BaseTeam> teamsWithEgg;

    private Map<Player, BaseTeam> teamPerPlayer;
    private Map<BaseTeam, Set<Player>> playersInTeams;

    private Set<Player> players;
    private Set<Player> playersLiving;

    private GameState state = GameState.NONE;
    private int currentTaskId = -1;

    public GameMap(
        IntObjectHashMap<ClickableBlock> clickableBlocks,
        ClickableSignGenerator[] generators,
        Map<BaseTeam, BlockLocation> spawns,
        Map<BaseTeam, BlockLocation> eggs,
        EntityLocation[] shopSpawns,
        int maxPersonsPerTeam,
        int borderSize,
        int id
    ) {
        this.clickableBlocks = clickableBlocks;
        this.generators = generators;
        this.spawns = spawns;
        this.eggs = eggs;
        this.shopSpawns = shopSpawns;
        this.maxPersonsPerTeam = maxPersonsPerTeam;
        this.borderSize = borderSize;
        this.shopkeepersID = new int[shopSpawns.length];
        this.id = id;
    }

    public void resetData() {
        this.players = new HashSet<>();
        this.teamPerPlayer = new HashMap<>();
        this.teamsWithEgg = new HashSet<>();
        this.playersInTeams = new HashMap<>();
        this.playersLiving = new HashSet<>();
        this.generatorsNeedUpdate = true;
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

    public Map<Player, BaseTeam> getTeamPerPlayer() {
        return teamPerPlayer;
    }

    public Set<BaseTeam> getTeamsWithEgg() {
        return teamsWithEgg;
    }

    public GameState getState() {
        return state;
    }

    public int[] getShopIDs() {
        return shopkeepersID;
    }

    public int getTaskId() {
        return currentTaskId;
    }

    public BlockLocation getSpawn(final BaseTeam team) {
        return spawns.get(team);
    }

    public Map<BaseTeam, BlockLocation> getEggs() {
        return eggs;
    }

    public Map<BaseTeam, BlockLocation> getSpawns() {
        return spawns;
    }

    public boolean generatorsNeedUpdate() {
        return generatorsNeedUpdate;
    }

    public void setGeneratorsNeedUpdate(boolean update) {
        this.generatorsNeedUpdate = update;
    }
    
    public ClickableSignGenerator[] getGenerators() {
        return generators;
    }

    public IntObjectHashMap<ClickableBlock> getClickableBlocks() {
        return clickableBlocks;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getMaxPersonsPerTeam() {
        return maxPersonsPerTeam;
    }

    public Map<BaseTeam, Set<Player>> getPlayersInTeam() {
        return playersInTeams;
    }

    public Set<Player> getPlayersLiving() {
        return playersLiving;
    }

    public EntityLocation[] getShopSpawns() {
        return shopSpawns;
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