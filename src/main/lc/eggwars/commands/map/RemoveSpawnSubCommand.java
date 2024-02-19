package lc.eggwars.commands.map;

import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class RemoveSpawnSubCommand implements Command {
    private final MapCreatorData data;

    RemoveSpawnSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            send(player, "&cTo use this command enable the editor mode");
            return;
        }

        final Set<Material> airBlocksStorage = null;
        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);

        if (targetBlock.getType() != Material.DIAMOND_BLOCK) {
            send(player, "&cTo remove a spawn, you need view a diamond block");
            return;
        };

        final BlockLocation location = BlockLocation.toBlockLocation(targetBlock.getLocation());
        final Set<Entry<BaseTeam, BlockLocation>> entries = creatorData.getSpawnsMap().entrySet();

        for (Entry<BaseTeam, BlockLocation> entry : entries) {
            if (entry.getValue().equals(location)) {
                targetBlock.setType(Material.AIR);
                send(player, "&aSpawn removed for the team " + entry.getKey().getKey());
                creatorData.getSpawnsMap().remove(entry.getKey());
                return;
            }
        }

        send(player, "&cThis isn't a spawn for any team");
    }
}