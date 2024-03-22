package lc.eggwars.game.clickable;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.game.generators.BaseGenerator;
import lc.eggwars.game.generators.GeneratorEntityItem;
import lc.eggwars.game.generators.GeneratorStorage;
import lc.eggwars.game.generators.TemporaryGenerator;
import lc.eggwars.messages.Messages;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.InventoryUtils;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class ClickableSignGenerator implements ClickableBlock {

    private final BlockLocation loc, min, max;
    private final int defaultLevel;
    private final BaseGenerator base;

    private TemporaryGenerator temporaryGenerator;

    public ClickableSignGenerator(BlockLocation loc, BlockLocation min, BlockLocation max, int defaultLevel, BaseGenerator base) {
        this.loc = loc;
        this.min = min;
        this.max = max;
        this.defaultLevel = defaultLevel;
        this.base = base;
    }

    @Override
    public void onClick(final Player player, final Action action) {
        if (temporaryGenerator == null || action != Action.RIGHT_CLICK_BLOCK || !player.getWorld().equals(temporaryGenerator.getWorld().getWorld())) {
            return;
        }
        if (player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }
        if (temporaryGenerator.getLevel() == base.maxlevel()) {
            Messages.send(player, "generator.max");
            return;
        }

        final PlayerInventory inventory = ((CraftPlayer)player).getHandle().inventory;
        final int amount = InventoryUtils.getAmount(base.drop(), inventory);
        final int needAmount = base.levels()[temporaryGenerator.getLevel()].upgradeItems();

        if (amount < needAmount) {
            player.sendMessage(Messages.get("generator.need").replace("%amount%", String.valueOf(needAmount)));
            return;
        }

        InventoryUtils.removeAmount(needAmount, base.drop(), inventory);

        temporaryGenerator.levelUp();
        GeneratorStorage.getStorage().setLines(temporaryGenerator.getWorld().getWorld().getBlockAt(loc.x(), loc.y(), loc.z()), base, temporaryGenerator.getLevel());
        Messages.send(player, "generator.levelup");
    }

    public void cleanData() {
        this.temporaryGenerator = null;
    }

    public void setGenerator(final World world, final int id) {
        final GeneratorEntityItem item = new GeneratorEntityItem();
        item.locX = loc.x();
        item.locY = loc.y();
        item.locZ = loc.z();

        item.setCustomNameVisible(true);
        item.setItemStack(base.drop());

        this.temporaryGenerator = new TemporaryGenerator(defaultLevel, base, item, world, id, this);
    }

    public TemporaryGenerator getGenerator() {
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

    @Override
    public boolean supportLeftClick() {
        return false;
    }

    public BlockLocation getMinLocation() {
        return min;
    }

    public BlockLocation getMaxLocation() {
        return max;
    }
}