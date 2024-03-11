package lc.eggwars.commands.map;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.utils.BlockLocation;
import lc.lcspigot.commands.Command;

interface MapSubCommand extends Command {

    @Override
    default void handle(CommandSender sender, String[] args) {
        return;
    }

    public void handle(final Player player, final String[] args, final CreatorData data);

    public default BlockLocation getBlock(final Player player, final Material target) {
        final Set<Material> airBlocksStorage = null;
        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        if (targetBlock.getType() != target) {
            return null;
        };
        return BlockLocation.toBlockLocation(targetBlock.getLocation());
    }
}