package lc.eggwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.lcspigot.commands.Command;

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
        PlayerDataStorage.getStorage().get(player.getUniqueId()).coins = 100_000;
    }
}