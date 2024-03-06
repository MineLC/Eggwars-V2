package lc.eggwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;
import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData;
import lc.eggwars.game.shop.shopkeepers.ShopkeepersData.Skin;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.Kit;
import lc.eggwars.others.kits.KitStorage;
import lc.lcspigot.commands.Command;

public final class InfoCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            send(sender, "You need be a user to use this command");
            return;
        } 

        final String format = Messages.get("commands.info");
        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        final Kit kit = KitStorage.getStorage().kitsPerId().get(data.kitSelected);
        final String kitName = (data.kitSelected == 0 || kit == null) ? "Ninguno" : kit.name();
        final Skin skin = ShopKeepersStorage.getStorage().skins().get(data.skinSelected);
        final String skinName =  (skin == null)
            ? ShopKeepersStorage.getStorage().skins().get(ShopkeepersData.VILLAGER_SKIN).name()
            : skin.name();
        
        send(sender, format
            .replace("%kills%", String.valueOf(data.kills))
            .replace("%deaths%", String.valueOf(data.deaths))
            .replace("%eggs%", String.valueOf(data.destroyedEggs))
            .replace("%finaldeaths%", String.valueOf(data.finalDeaths))
            .replace("%finalkills%", String.valueOf(data.finalKills))
            .replace("%level%", String.valueOf(data.level))
            .replace("%kit%", kitName)
            .replace("%shopskin%", skinName)
            .replace("%kdr%", (data.deaths == 0) ? String.valueOf(data.kills) : String.valueOf((float)(data.kills / data.deaths)))
            .replace("%coins%", String.valueOf(data.coins))
            .replace("%wins%", String.valueOf(data.wins))
        );
    }
}