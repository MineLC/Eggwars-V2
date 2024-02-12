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
import lc.eggwars.utils.ItemUtils;
import net.minecraft.server.v1_8_R3.PlayerInventory;

public final class ClickableSignGenerator implements ClickableBlock {

    private final BlockLocation loc;
    private final int defaultLevel;
    private final BaseGenerator base;

    private TemporaryGenerator temporaryGenerator;
    private World world;

    public ClickableSignGenerator(BlockLocation loc, int defaultLevel, BaseGenerator base) {
        this.loc = loc;
        this.defaultLevel = defaultLevel;
        this.base = base;
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
        if (temporaryGenerator.getLevel() == base.maxlevel()) {
            Messages.send(player, "generator.max");
            return;
        }

        final PlayerInventory inventory = ((CraftPlayer)player).getHandle().inventory;
        final int amount = ItemUtils.getAmount(base.drop(), inventory);
        final int needAmount = base.levels()[temporaryGenerator.getLevel()].upgradeItems();

        if (amount < needAmount) {
            player.sendMessage(Messages.get("generator.need").replace("%amount%", String.valueOf(needAmount)));
            return;
        }

        ItemUtils.removeAmount(needAmount, base.drop(), inventory);

        temporaryGenerator.levelUp();
        GeneratorStorage.getStorage().setLines(world.getBlockAt(loc.x(), loc.y(), loc.z()), base, temporaryGenerator.getLevel());
        Messages.send(player, "generator.levelup");
    }
}