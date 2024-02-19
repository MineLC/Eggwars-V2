package lc.eggwars.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.utils.IntegerUtils;
import lc.lcspigot.commands.Command;

final class SetMaxPersonsSubCommand implements Command {

    private final MapCreatorData data;

    SetMaxPersonsSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            send(sender, "&cTo use this command enable the editor mode");
            return;
        }
        if (args.length != 2) {
            send(sender, "&cFormat: /map setmax &7(number)");
            return;
        }

        final int max = IntegerUtils.parsePositive(args[1]);
        if (max == -1) {
            send(sender, "Max persons per team can't be negative");
            return;
        }

        creatorData.setMaxPersonsPerTeam(max);
        send(sender, "Max persons per team set in " + max);
    }
}