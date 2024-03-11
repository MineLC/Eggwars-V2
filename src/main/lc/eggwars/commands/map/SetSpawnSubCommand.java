package lc.eggwars.commands.map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;

final class SetSpawnSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        if (args.length != 2) {
            sendWithColor(player, "&cFormat: /map setspawn &7(team)");
            return;
        }

        final BaseTeam team = TeamStorage.getStorage().getTeam(args[1]);

        if (team == null) {
            sendWithColor(player, "The team " + args[1] + " dont exist. List: " + TeamStorage.getStorage().getTeamsName());
            return;
        }
        final BlockLocation location = getBlock(player, Material.DIAMOND_BLOCK);
        if (location == null) {
            sendWithColor(player, "&cTo set a new spawn, you need view a diamond block");
            return;
        };
        data.setSpawn(team, location);
        sendWithColor(player, "&aSpawn added for the team " + args[1]);
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 2) ? (String[])TeamStorage.getStorage().getTeamsName().toArray() : none();
    }
}