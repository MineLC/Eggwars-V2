package lc.eggwars.commands.map;

import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class RemoveSpawnSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final BlockLocation targetBlock = getBlock(player, Material.DIAMOND_BLOCK);

        if (targetBlock == null) {
            sendWithColor(player, "&cTo remove a spawn, you need view a diamond block");
            return;
        };

        final Set<Entry<BaseTeam, BlockLocation>> entries = data.getSpawnsMap().entrySet();

        for (Entry<BaseTeam, BlockLocation> entry : entries) {
            if (entry.getValue().equals(targetBlock)) {
                sendWithColor(player, "&aSpawn removed for the team " + entry.getKey().getKey());
                data.getSpawnsMap().remove(entry.getKey());
                return;
            }
        }

        sendWithColor(player, "&cThis isn't a spawn for any team");
    }
}