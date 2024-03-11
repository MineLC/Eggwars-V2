package lc.eggwars.commands.map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.EntityLocation;

final class RemoveShoopkeperSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final BlockLocation targetBlock = getBlock(player, Material.IRON_BLOCK);

        if (targetBlock == null) {
            sendWithColor(player, "&cTo remove a shopkeeper spawn, you need view a iron block");
            return;
        }

        final EntityLocation location = EntityLocation.toEntityLocation(targetBlock.toLocation(player.getWorld()), player.getLocation().getYaw(), player.getLocation().getPitch());
        if (!data.getShopKeepersSpawns().contains(location)) {
            sendWithColor(player, "&cIn this site don't exist a shopkeeper spawn");
            return;
        }

        data.getShopKeepersSpawns().remove(location);
        sendWithColor(player, "&aShopkeeper spawn removed!");
    }
}
