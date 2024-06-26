package lc.eggwars.game.generators;

import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.World;

import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.utils.BlockLocation;

public final class TemporaryGenerator {

    private final int height;
    private final int id;
    private final BaseGenerator base;
    private final Entity entityItem;
    private final World world;
    private final ClickableSignGenerator data;

    private int waitedTime = 0;
    private int itemsAmount = 0;

    private BaseGenerator.Level currentLevel;
    private int level;
    public boolean needDeletePacket = false;

    public TemporaryGenerator(
        int level,
        BaseGenerator base,
        GeneratorEntityItem entityItem,
        org.bukkit.World world,
        int id,
        ClickableSignGenerator data
    ) {
        this.currentLevel = base.levels()[level];
        this.level = level;
        this.base = base;
        this.height = data.getLocation().y() >> 4;
        this.entityItem = entityItem;
        this.world = ((CraftWorld)world).getHandle();
        this.id = id;
        this.data = data;
    }

    public void levelUp() {
        this.currentLevel = base.levels()[++level];
    }

    public void setAmount(int amount) {
        this.itemsAmount = amount;
    }

    public void resetWaitedTime() {
        this.waitedTime = 0;
    }

    public void addItemsToGenerate() {
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
        return currentLevel.refreshEvery() == waitedTime;
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
        return data.getLocation();
    }

    public Entity getEntityItem() {
        return entityItem;
    }

    public ClickableSignGenerator getData() {
        return data;
    }

    @Override
    public int hashCode() {
        return id;
    }
}