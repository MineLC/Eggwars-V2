package lc.eggwars.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public interface SubCommand {
    void execute(final CommandSender sender, final String[] args);
    List<String> onTab(final CommandSender sender, final String[] args);

    default void send(final CommandSender sender, final String message) {
        sender.sendMessage(message.replace('&', ChatColor.COLOR_CHAR));
    }
}