package lc.eggwars.commands.map;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.commands.SubCommand;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.utils.BlockLocation;

final class RemoveGeneratorSubCommand implements SubCommand {

    private final MapCreatorData data;

    RemoveGeneratorSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            send(player, "&cTo use this command enable the editor mode");
            return;
        }
        final Set<Material> airBlocksStorage = null;

        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        final Material material = targetBlock.getType();

        if (material != Material.WALL_SIGN && material != Material.SIGN_POST) {
            send(player, "&cTo remove a generator, you need view a sign");
            return;
        }

        final BlockLocation location = BlockLocation.toBlockLocation(targetBlock.getLocation());
        if (!creatorData.removeGenerator(location)) {
            send(player, "&cThis sign isn't a generator");
            return;
        }
        final Sign sign = (Sign)targetBlock.getState();
        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");
        sign.update();

        send(player, "&aGenerator removed!");
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}