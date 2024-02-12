package lc.eggwars.commands.map;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import lc.eggwars.commands.BasicSubCommand;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.EntityLocation;

final class RemoveShoopkeperSubCommand implements BasicSubCommand {

    private final MapCreatorData data;

    RemoveShoopkeperSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void execute(Player player, String[] args) {
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            send(player, "&cTo use this command enable the editor mode");
            return;
        }
        final Set<Material> airBlocksStorage = null;

        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        final Material material = targetBlock.getType();

        if (material != Material.BARRIER) {
            send(player, "&cTo remove a shopkeeper spawn, you need view a invisible block");
            return;
        }

        final EntityLocation location = EntityLocation.toEntityLocation(targetBlock.getLocation());
        if (!creatorData.getShopKeepersSpawns().contains(location)) {
            send(player, "&cIn this site don't exist a shopkeeper spawn");
            return;
        }

        creatorData.getShopKeepersSpawns().remove(location);
        send(player, "&aShopkeeper spawn removed!");
    }
}
