package lc.eggwars.commands.map;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.utils.EntityLocation;

final class AddShopkeeperSubCommand implements Command {

    private final MapCreatorData data;

    AddShopkeeperSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            sendWithColor(player, "&cTo use this command enable the editor mode");
            return;
        }
        final Set<Material> airBlocksStorage = null;

        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        final Material material = targetBlock.getType();

        if (material != Material.BARRIER) {
            sendWithColor(player, "&cTo add a shopkeeper spawn, you need view a invisible block");
            return;
        }

        final EntityLocation location = EntityLocation.toEntityLocation(targetBlock.getLocation(), player.getLocation().getYaw());
        if (creatorData.getShopKeepersSpawns().contains(location)) {
            sendWithColor(player, "&cIn this site already exist a shopkeeper spawn. Use /map removeshopspawn");
            return;
        }

        creatorData.getShopKeepersSpawns().add(location);
        sendWithColor(player, "&aShopkeeper spawn added!");
    }
}