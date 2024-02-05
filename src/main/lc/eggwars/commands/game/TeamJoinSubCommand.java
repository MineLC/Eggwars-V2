package lc.eggwars.commands.game;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.commands.SubCommand;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;

final class TeamJoinSubCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        if (args.length != 2) {
            send(sender, "Format: /game teamjoin (teamname)");
            return;
        }
        final BaseTeam team = TeamStorage.getStorage().getTeam(args[1]);
        if (team == null) {
            send(sender, "The team " + args[1] + " don't exist");
            return;
        }
        final GameMap game = GameStorage.getStorage().getGame(player.getUniqueId());

        if (game == null) {
            send(sender, "You aren't in a game");
            return;
        }

        if (game.getState() == GameState.IN_GAME) {
            send(sender, "This game already started");
            return;
        }

        game.getPlayersPerTeam().remove(player);
        game.getPlayersPerTeam().put(player, team);
        send(sender, "You are in the team");
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}