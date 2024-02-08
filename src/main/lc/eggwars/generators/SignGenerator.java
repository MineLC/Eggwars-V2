package lc.eggwars.generators;

import java.util.List;

import org.bukkit.entity.Player;

import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityItem;

public final class SignGenerator implements ClickableBlock {

    private final BlockLocation spawnLocation;
    private final BaseGenerator base;
    private final EntityItem item;
    private final int defaultLevel;
    private final int height;

    private Chunk[] radiusChunk;
    private int currentLevel = 0;
    private int itemAmount = 0;

    public SignGenerator(BlockLocation spawnLocation, BaseGenerator base, EntityItem item, int currentLevel) {
        this.spawnLocation = spawnLocation;
        this.base = base;
        this.item = item;
        this.height = spawnLocation.y() >> 4;
        this.defaultLevel = currentLevel;
        this.currentLevel = currentLevel;
    }

    public void updateChunks(final Chunk[] radiusChunk) {
        this.radiusChunk = radiusChunk;
    }

    public boolean levelup() {
        if (currentLevel + 1 <= base.maxLevel()) {
            currentLevel++;
            return true;
        }
        return false;
    }

    public void setDefaultLevel() {
        this.currentLevel = defaultLevel;
    }

    public Chunk[] getChunks() {
        return radiusChunk;
    }

    public EntityItem getItem() {
        return item;
    }

    public int getLevel() {
        return currentLevel;
    }

    public List<Entity> getEntities(final Chunk chunk) {
        return chunk.entitySlices[height];
    }

    public BlockLocation getLocation() {
        return spawnLocation;
    }

    public BaseGenerator getBase() {
        return base;
    }

    public int getAmount() {
        return itemAmount;
    }

    public void setAmount(final int amount) {
        itemAmount = amount;
    }

    @Override
    public void onClick(Player player) {
        player.sendMessage("No pongan esa cumbia");
    }
}