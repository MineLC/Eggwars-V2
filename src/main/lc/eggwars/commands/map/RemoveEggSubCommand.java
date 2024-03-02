package lc.eggwars.commands.map;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class RemoveEggSubCommand implements Command {
    private final MapCreatorData data;

    RemoveEggSubCommand(MapCreatorData data) {
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

        if (targetBlock.getType() != Material.DRAGON_EGG) {
            sendWithColor(player, "&cTo remove a team egg, you need view a dragon egg");
            return;
        };

        final BlockLocation location = BlockLocation.toBlockLocation(targetBlock.getLocation());
        final Set<Entry<BaseTeam, BlockLocation>> entries = creatorData.getEggsMap().entrySet();

        for (Entry<BaseTeam, BlockLocation> entry : entries) {
            if (entry.getValue().equals(location)) {
                targetBlock.setType(Material.AIR);
                sendWithColor(player, "&aEgg removed for the team " + entry.getKey().getKey());
                creatorData.getEggsMap().remove(entry.getKey());
                return;
            }
        }

        sendWithColor(player, "&cThis isn't a egg for any team");
    }
}