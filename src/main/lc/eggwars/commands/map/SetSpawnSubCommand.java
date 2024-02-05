package lc.eggwars.commands.map;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.commands.SubCommand;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;

final class SetSpawnSubCommand implements SubCommand {

    private MapCreatorData data;

    SetSpawnSubCommand(MapCreatorData data) {
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

        if (args.length != 2) {
            send(sender, "&cFormat: /map addspawn (team)");
            return;
        }

        final BaseTeam team = TeamStorage.getStorage().getTeam(args[1]);

        if (team == null) {
            send(sender, "The team " + args[1] + " dont exist. List: " + TeamStorage.getStorage().getTeamsName());
            return;
        }
        final Set<Material> airBlocksStorage = null;
        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);

        if (targetBlock.getType() != Material.DIAMOND_BLOCK) {
            send(player, "&cTo set a new spawn, you need view a diamond block");
            return;
        };
        creatorData.setSpawn(team, BlockLocation.toBlockLocation(targetBlock.getLocation()));
        send(sender, "&aSpawn added for the team " + args[1]);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return (args.length == 2) ? List.copyOf(TeamStorage.getStorage().getTeamsName()) : List.of();
    }
}