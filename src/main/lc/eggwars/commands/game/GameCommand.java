package lc.eggwars.commands.game;

import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.lcspigot.commands.Command;

import net.md_5.bungee.api.ChatColor;

public final class GameCommand implements Command {

    private final JoinSubCommand join;
    private final LeaveSubCommand leave;

    public GameCommand(EggwarsPlugin plugin) {
        this.join = new JoinSubCommand(plugin);
        this.leave = new LeaveSubCommand();
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            send(sender, "You need be a player to use map creator");
            return;
        } 
        if (args.length < 1) {
            send(sender, format());
            return;
        }
    
        switch (args[0].toLowerCase()) {
            case "join":
                join.handle(player, args);
                break;
            case "leave":
                leave.handle(player, args);
                break;
            default:
                send(sender, format());
                break;
        }
        return;
    }   

    private String format() {
        return """
              &5&lGame command &7(MineLC)
              &r
              &d/game &7->
                &ejoin &7(mapName) - &fJoin to a map
                &eteamjoin &7(team) - &fJoin in a team
                &r
                """.replace('&', ChatColor.COLOR_CHAR);
    }
}