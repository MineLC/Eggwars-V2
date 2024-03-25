package lc.eggwars.game.threadtasks;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapData;

public final class UpdateShopkeepersTask {

    public void execute(final MapData map, final long time) {
        if (map.getGameInProgress().getState() == GameState.IN_GAME) {
            update(map.getGameInProgress(), time);
        }
    }

    private void update(final GameInProgress game, final long time) {
        final long elapseSeconds = (time - game.getStartedTime()) / 1000;
        if (elapseSeconds % 30 == 0) {
            new ShopKeeperManager().send(game.getPlayers(),game);
        }
    }
}