package lc.eggwars.game.generators;

import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

import java.util.List;


import lc.eggwars.utils.BlockLocation;

public final class TemporaryGenerator {

    private final int height;
    private final BaseGenerator base;

    private final Entity entityItem;

    private int waitedTime = 0;
    private int itemsAmount = 0;

    private BaseGenerator.Level currentLevel;
    private int level;

    private World world;
    private Chunk[] chunks;

    private final BlockLocation location;

    public TemporaryGenerator(int level, BaseGenerator base, Entity entityItem, BlockLocation location) {
        this.currentLevel = base.levels()[level];
        this.level = level;
        this.base = base;
        this.height = location.y() >> 4;
        this.entityItem = entityItem;
        this.location = location;
    }

    public void levelUp() {
        this.currentLevel = base.levels()[++level];
    }

    public void update(World world, Chunk[] chunks) {
        this.world = world;
        this.chunks = chunks;
    }

    public void setAmount(int amount) {
        this.itemsAmount = amount;
    }

    public void resetWaitedTime() {
        this.waitedTime = 0;
    }

    public void addItem() {
        itemsAmount += currentLevel.itemsToGenerate();
    }

    public void addOneSecond() {
        ++waitedTime;
    }

    public int getHeight() {
        return height;
    }

    public int getAmount() {
        return itemsAmount;
    }

    public boolean canRefreshItem() {
        return (currentLevel.refreshEvery() == waitedTime);
    }

    public int getWaitedTime() {
        return waitedTime;
    }

    public int getWaitToSpawn() {
        return currentLevel.waitingTime();
    }

    public World getWorld() {
        return world;
    }

    public List<Entity> getEntities(Chunk chunk) {
        return chunk.entitySlices[height];
    }

    public int getRefreshEvery() {
        return currentLevel.refreshEvery();
    }

    public int getEntitiesInNearbyChunks() {
        return getEntities(chunks[0]).size() +
            getEntities(chunks[1]).size() +
            getEntities(chunks[2]).size() +
            getEntities(chunks[3]).size() +
            getEntities(chunks[4]).size() +
            getEntities(chunks[5]).size() +
            getEntities(chunks[6]).size() +
            getEntities(chunks[7]).size() +
            getEntities(chunks[8]).size();
    }

    public Chunk[] getChunks() {
        return chunks;
    }

    public ItemStack getItem() {
        return base.drop();
    }

    public BaseGenerator getBase() {
        return base;
    }

    public int getLevel() {
        return level;
    }

    public BlockLocation loc() {
        return location;
    }

    public Entity getEntityItem() {
        return entityItem;
    }
}