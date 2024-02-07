package lc.eggwars.commands.map;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import lc.eggwars.commands.BasicSubCommand;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class RemoveEggSubCommand implements BasicSubCommand {
    private final MapCreatorData data;

    RemoveEggSubCommand(MapCreatorData data) {
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

        if (targetBlock.getType() != Material.DRAGON_EGG) {
            send(player, "&cTo remove a team egg, you need view a dragon egg");
            return;
        };

        final BlockLocation location = BlockLocation.toBlockLocation(targetBlock.getLocation());
        final Set<Entry<BaseTeam, BlockLocation>> entries = creatorData.getEggsMap().entrySet();

        for (Entry<BaseTeam, BlockLocation> entry : entries) {
            if (entry.getValue().equals(location)) {
                targetBlock.setType(Material.AIR);
                send(player, "&aEgg removed for the team " + entry.getKey().getKey());
                creatorData.getSpawnsMap().remove(entry.getKey());
                return;
            }
        }

        send(player, "&cThis isn't a egg for any team");
    }
}