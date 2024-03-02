package lc.eggwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import obed.me.minecore.objects.Jugador;

public final class GiveCoin implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "test")) {
            return;
        }
        if (args.length != 1) {
            send(sender, "/givecoin (player)");
            return;
        }
        final Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            send(sender, "JUGADOR INEXISTENTE");
            return;
        }
        Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars().setLCoins(100000);
    }
}