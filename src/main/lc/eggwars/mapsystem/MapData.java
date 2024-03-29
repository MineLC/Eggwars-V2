package lc.eggwars.mapsystem;

import java.util.Map;

import gnu.trove.set.hash.TIntHashSet;
import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.EntityLocation;

public final class MapData {
    private final IntObjectHashMap<ClickableBlock> clickableBlocks;
    private final Map<BaseTeam, BlockLocation> spawns;
    private final Map<BaseTeam, BlockLocation> eggs;
    private final ClickableSignGenerator[] generators;
    private final TIntHashSet shopKeepersID;
    private final EntityLocation[] shopSpawns;
    private final int maxPlayers;
    private final int maxPersonsPerTeam;
    private final int borderSize;
    private final int id;

    private GameInProgress gameInProgress;

    MapData(
        IntObjectHashMap<ClickableBlock> clickableBlocks,
        Map<BaseTeam, BlockLocation> spawns,
        Map<BaseTeam, BlockLocation> eggs,
        ClickableSignGenerator[] generators,
        TIntHashSet shopKeepersID,
        EntityLocation[] shopSpawns,
        int maxPersonsPerTeam,
        int borderSize,
        int id
    ) {
        this.clickableBlocks = clickableBlocks;
        this.spawns = spawns;
        this.eggs = eggs;
        this.generators = generators;
        this.shopSpawns = shopSpawns;
        this.shopKeepersID = shopKeepersID;
        this.maxPersonsPerTeam = maxPersonsPerTeam;
        this.borderSize = borderSize;
        this.maxPlayers = maxPersonsPerTeam * spawns.keySet().size();
        this.id = id;
    }

    public void setGame(GameInProgress game) {
        this.gameInProgress = game;
    }

    public GameInProgress getGameInProgress() {
        return gameInProgress;
    }

    public IntObjectHashMap<ClickableBlock> getClickableBlocks() {
        return clickableBlocks;
    }

    public Map<BaseTeam, BlockLocation> getSpawns() {
        return spawns;
    }

    public Map<BaseTeam, BlockLocation> getEggs() {
        return eggs;
    }

    public ClickableSignGenerator[] getGenerators() {
        return generators;
    }

    public EntityLocation[] getShopSpawns() {
        return shopSpawns;
    }

    public TIntHashSet getShopsID() {
        return shopKeepersID;
    }

    public int getMaxPersonsPerTeam() {
        return maxPersonsPerTeam;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public final int hashCode() {
        return id;
    }

    @Override
    public final boolean equals(Object object) {
        return (object instanceof MapData otherMapData) ? otherMapData.id == this.id : false;
    }
}