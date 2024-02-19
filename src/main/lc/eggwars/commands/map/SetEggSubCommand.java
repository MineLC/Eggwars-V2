package lc.eggwars.commands.map;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;

final class SetEggSubCommand implements Command {

    private MapCreatorData data;

    SetEggSubCommand(MapCreatorData data) {
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

        if (args.length != 2) {
            send(player, "&cFormat: /map setegg &7(team)");
            return;
        }

        final BaseTeam team = TeamStorage.getStorage().getTeam(args[1]);

        if (team == null) {
            send(player, "The team " + args[1] + " dont exist. List: " + TeamStorage.getStorage().getTeamsName());
            return;
        }
        final Set<Material> airBlocksStorage = null;
        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);

        if (targetBlock.getType() != Material.DRAGON_EGG) {
            send(player, "&cTo set a team egg, you need view a dragon egg");
            return;
        };
        creatorData.setEgg(team, BlockLocation.toBlockLocation(targetBlock.getLocation()));
        send(player, "&aEgg added for the team " + args[1]);
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 2) ? (String[]) TeamStorage.getStorage().getTeamsName().toArray() : none();
    }
}