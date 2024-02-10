package lc.eggwars.commands.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import net.md_5.bungee.api.ChatColor;

public final class GameCommand implements TabExecutor {

    private final JoinSubCommand join;
    private final TeamJoinSubCommand team;
    private final LeaveSubCommand leave;

    public GameCommand(EggwarsPlugin plugin) {
        this.join = new JoinSubCommand(plugin);
        this.team = new TeamJoinSubCommand();
        this.leave = new LeaveSubCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {   
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You need be a player to use map creator");
            return true;
        } 
        if (args.length < 1) {
            sender.sendMessage(format());
            return true;
        }
    
        switch (args[0].toLowerCase()) {
            case "join":
                join.execute(player, args);
                break;
            case "teamjoin":
                team.execute(player, args);
                break;
            case "leave":
                leave.execute(player, args);
                break;
            default:
                sender.sendMessage(format());
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
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