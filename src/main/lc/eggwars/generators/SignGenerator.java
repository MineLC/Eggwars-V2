package lc.eggwars.generators;

import org.bukkit.entity.Player;

import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;

public final class SignGenerator implements ClickableBlock {

    private final BlockLocation spawnLocation;
    private final BaseGenerator base;
    private int currentLevel = 0;

    public SignGenerator(BlockLocation spawnLocation, BaseGenerator base, int currentLevel) {
        this.spawnLocation = spawnLocation;
        this.base = base;
        this.currentLevel = currentLevel;
    }

    public boolean levelup() {
        if (currentLevel + 1 <= base.maxLevel()) {
            currentLevel++;
            return true;
        }
        return false;
    }
    
    public int getLevel() {
        return currentLevel;
    }

    public BlockLocation getLocation() {
        return spawnLocation;
    }

    public BaseGenerator getBase() {
        return base;
    }

    @Override
    public void onClick(Player player) {
        player.sendMessage("No pongan esa cumbia");
    }
}