package lc.eggwars.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface BasicSubCommand extends SubCommand {

    @Override
    default List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}