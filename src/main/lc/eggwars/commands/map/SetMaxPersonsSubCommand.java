package lc.eggwars.commands.map;

import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.utils.IntegerUtils;

final class SetMaxPersonsSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        if (args.length != 2) {
            sendWithColor(player, "&cFormat: /map setmax &7(number)");
            return;
        }

        final int max = IntegerUtils.parsePositive(args[1]);
        if (max == -1) {
            sendWithColor(player, "Max persons per team can't be negative");
            return;
        }

        data.setMaxPersonsPerTeam(max);
        sendWithColor(player, "Max persons per team set in " + max);
    }
}