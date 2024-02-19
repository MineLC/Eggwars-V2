package lc.eggwars.commands.game;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;

final class TeamJoinSubCommand implements Command
 {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        if (args.length < 2) {
            send(player, "&cFormat: /game teamjoin (teamname)");
            return;
        }
        final BaseTeam team = TeamStorage.getStorage().getTeam(args[1]);
        if (team == null) {
            send(player, "&cThe team " + args[1] + " don't exist");
            return;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());

        if (game == null) {
            send(player, "&cYou aren't in a game");
            return;
        }

        if (game.getState() == GameState.IN_GAME) {
            send(player, "&cThis game already started");
            return;
        }

        if (game.getMapData().getSpawns().get(team) == null) {
            send(player, "&cThis team don't exist");
            return;
        }

        Set<Player> players = game.getPlayersInTeam().get(team);

        if (players == null) {
            players = new HashSet<>();
            game.getPlayersInTeam().put(team, players);
        }

        if (players.size() == game.getMapData().getMaxPersonsPerTeam()) {
            Messages.send(player, "team.full");
            return;
        }

        players.add(player);

        game.getTeamPerPlayer().remove(player);
        game.getTeamPerPlayer().put(player, team);
        team.getTeam().addPlayer(player);

        player.sendMessage(Messages.get("team.join").replace("%team%", team.getName()));
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return none();
        }
        if (!(sender instanceof Player player)) {
            return none();
        }
        final GameInProgress map = GameStorage.getStorage().getGame(player.getUniqueId());
        return (map == null) ? none() : (String[])map.getMapData().getSpawns().keySet().stream().map(BaseTeam::getKey).toArray();
    }
}