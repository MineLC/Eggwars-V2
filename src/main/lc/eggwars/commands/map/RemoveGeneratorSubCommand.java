package lc.eggwars.commands.map;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.utils.BlockLocation;

final class RemoveGeneratorSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final Set<Material> airBlocksStorage = null;

        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        final Material material = targetBlock.getType();

        if (material != Material.WALL_SIGN && material != Material.SIGN_POST) {
            sendWithColor(player, "&cTo remove a generator, you need view a sign");
            return;
        }

        final BlockLocation location = BlockLocation.toBlockLocation(targetBlock.getLocation());
        if (!data.removeGenerator(location)) {
            sendWithColor(player, "&cThis sign isn't a generator");
            return;
        }
        final Sign sign = (Sign)targetBlock.getState();
        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");
        sign.update();

        sendWithColor(player, "&aGenerator removed!");
    }
}