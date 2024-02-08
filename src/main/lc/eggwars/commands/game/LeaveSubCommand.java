package lc.eggwars.commands.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.eggwars.commands.BasicSubCommand;
import lc.eggwars.game.GameStorage;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.spawn.SpawnStorage;

final class LeaveSubCommand implements BasicSubCommand {

    @Override
    public void execute(Player player, String[] args) {
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());
        if (map == null) {
            send(player, "Actualmente no estás en ningun juego");
            return;
        }
        player.teleport(SpawnStorage.getStorage().getLocation());
        player.setGameMode(GameMode.ADVENTURE);

        GameStorage.getStorage().leave(map, player);
        send(player, "Has salido del juego");
    }
}