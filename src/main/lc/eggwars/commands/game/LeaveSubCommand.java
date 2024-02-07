package lc.eggwars.commands.game;


import org.bukkit.entity.Player;

import lc.eggwars.commands.BasicSubCommand;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.teams.BaseTeam;

final class LeaveSubCommand implements BasicSubCommand {

    @Override
    public void execute(Player player, String[] args) {
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null) {
            return;
        }
        final BaseTeam team = map.getPlayersPerTeam().get(player);

        if (team != null) {
            team.getTeam().removePlayer(player);
        }

        if (GameStorage.getStorage().leave(map, player)) {
            send(player, "Has salido del juego");
            return;
        }
        send(player, "Actualmente no estás en ningun juego");
    }
}