package lc.eggwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.game.shop.shopkeepers.ShopKeepersStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.kits.Kit;
import lc.eggwars.others.kits.KitStorage;
import lc.lcspigot.commands.Command;
import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public final class InfoCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            send(sender, "You need be a user to use this command");
            return;
        } 

        final String format = Messages.get("commands.info");
        final StatsEggWars stats = Jugador.getJugador(sender.getName()).getServerStats().getStatsEggWars();
        final Kit kit = KitStorage.getStorage().kitsPerId().get(stats.getSelectedKit());
        String kitName = (kit == null) ? "Ninguno" : kit.name();

        send(sender, format
            .replace("%kills%", String.valueOf(stats.getKills()))
            .replace("%deaths%", String.valueOf(stats.getDeaths()))
            .replace("%eggs%", String.valueOf(stats.getDestroyedEggs()))
            .replace("%finaldeaths%", String.valueOf(stats.getLastDeath()))
            .replace("%finalkills%", String.valueOf(stats.getLastKill()))
            .replace("%level%", String.valueOf(stats.getLevel()))
            .replace("%kit%", kitName
            .replace("%shopskin%", ShopKeepersStorage.getStorage().skins().get(stats.getShopKeeperSkinSelected()).name())
            .replace("%kdr%", (stats.getDeaths() == 0) ? String.valueOf(stats.getKills()) : String.valueOf((float)(stats.getKills() / stats.getDeaths())))
            .replace("%coins%", String.valueOf(stats.getLCoins()))
            .replace("%games%", String.valueOf(stats.getPlayed()))
        ));
    }
}