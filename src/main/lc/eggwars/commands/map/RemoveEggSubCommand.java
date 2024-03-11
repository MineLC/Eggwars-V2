package lc.eggwars.commands.map;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class RemoveEggSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final BlockLocation targetBlock = getBlock(player, Material.DRAGON_EGG);

        if (targetBlock == null) {
            sendWithColor(player, "&cTo remove a team egg, you need view a dragon egg");
            return;
        };

        final Set<Entry<BaseTeam, BlockLocation>> entries = data.getEggsMap().entrySet();

        for (Entry<BaseTeam, BlockLocation> entry : entries) {
            if (entry.getValue().equals(targetBlock)) {
                sendWithColor(player, "&aEgg removed for the team " + entry.getKey().getKey());
                data.getEggsMap().remove(entry.getKey());
                return;
            }
        }

        sendWithColor(player, "&cThis isn't a egg for any team");
    }
}