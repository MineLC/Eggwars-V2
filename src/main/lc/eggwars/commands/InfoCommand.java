package lc.eggwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
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
        Player player;
        if (args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sendWithColor(sender, "&cEste jugador no existe");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                send(sender, "You need be a user to use this command");
                return;
            } else {
                player = (Player)sender;
            }
        }

        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        final Kit kit = KitStorage.getStorage().kitsPerId().get(data.kitSelected);
        final String kitName = (data.kitSelected == 0 || kit == null) ? "Ninguno" : kit.name();
        final Skin skin = ShopKeepersStorage.getStorage().skins().get(data.skinSelected);
        final String skinName =  (skin == null)
            ? ShopKeepersStorage.getStorage().skins().get(ShopkeepersData.VILLAGER_SKIN).name()
            : skin.name();
        
        send(sender, Messages.get("commands.info")
            .replace("%name%", player.getName())
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