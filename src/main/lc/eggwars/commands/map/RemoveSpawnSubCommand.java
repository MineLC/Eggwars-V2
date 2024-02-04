package lc.eggwars.commands.map;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.commands.SubCommand;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;

final class RemoveSpawnSubCommand implements SubCommand {
    private final MapCreatorData data;

    RemoveSpawnSubCommand(MapCreatorData data) {
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

        if (targetBlock.getType() != Material.DIAMOND_BLOCK) {
            send(player, "&cTo remove a spawn, you need view a diamond block");
            return;
        };
        final Location location = targetBlock.getLocation();
        final Set<Entry<BaseTeam, Set<Location>>> entries = creatorData.getSpawnsMap().entrySet();

        for (Entry<BaseTeam, Set<Location>> entry : entries) {
            if (entry.getValue().remove(location)) {
                targetBlock.setType(Material.AIR);
                send(sender, "&aSpawn removed for the team " + entry.getKey().getKey());
                return;
            }
        }
        send(sender, "&cThis isn't a spawn for any team");
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}