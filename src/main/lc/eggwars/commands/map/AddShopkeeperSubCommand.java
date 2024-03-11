package lc.eggwars.commands.map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.EntityLocation;

final class AddShopkeeperSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final BlockLocation location = getBlock(player, Material.IRON_BLOCK);
        if (location == null) {
            sendWithColor(player, "&cTo add a shopkeeper spawn, you need view a invisible block");
            return;
        }

        final EntityLocation entityLocation = EntityLocation.toEntityLocation(location.toLocation(player.getWorld()), player.getLocation().getYaw(), player.getLocation().getPitch());
        if (data.getShopKeepersSpawns().contains(entityLocation)) {
            sendWithColor(player, "&cIn this site already exist a shopkeeper spawn. Use /map removeshopspawn");
            return;
        }

        data.getShopKeepersSpawns().add(entityLocation);
        sendWithColor(player, "&aShopkeeper spawn added!");
    }
}