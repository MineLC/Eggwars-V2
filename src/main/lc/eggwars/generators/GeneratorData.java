package lc.eggwars.generators;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;

public final class GeneratorData implements ClickableBlock {

    private final BlockLocation loc;
    private final int defaultLevel;
    private final BaseGenerator base;

    private TemporaryGenerator temporaryGenerator;
    private World world;

    public GeneratorData(BlockLocation loc, int defaultLevel, BaseGenerator base) {
        this.loc = loc;
        this.defaultLevel = defaultLevel;
        this.base = base;
    }

    final TemporaryGenerator getGenerator() {
        return temporaryGenerator;
    }

    public BlockLocation getLocation() {
        return loc;
    }

    public int getDefaultLevel() {
        return defaultLevel;
    }

    public BaseGenerator getBase() {
        return base;
    }

    public void cleanData() {
        this.temporaryGenerator = null;
        this.world = null;
    }

    public void setGenerator(final World world) {
        final GeneratorEntityItem item = new GeneratorEntityItem();
        item.locX = loc.x() + 0.5D;
        item.locY = loc.y();
        item.locZ = loc.z() + 0.5D;

        item.setCustomNameVisible(true);
        item.setItemStack(base.drop());

        this.temporaryGenerator = new TemporaryGenerator(defaultLevel, base, item, loc);
        this.world = world;
    }

    @Override
    public void onClick(final Player player, final Action action) {
        if (temporaryGenerator == null || action != Action.RIGHT_CLICK_BLOCK || !player.getWorld().equals(world)) {
            return;
        }

        if (player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }

        if (temporaryGenerator.levelUp()) {
            GeneratorStorage.getStorage().setLines(world.getBlockAt(loc.x(), loc.y(), loc.z()), base, temporaryGenerator.getLevel());
            player.sendMessage("El generador ha subido de nivel");
            return;
        }
        player.sendMessage("El generador ya está al nivel máximo");
    }
}