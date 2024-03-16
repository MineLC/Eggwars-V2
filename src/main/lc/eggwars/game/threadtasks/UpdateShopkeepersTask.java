package lc.eggwars.game.threadtasks;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapData;

public final class UpdateShopkeepersTask {

    private final MapData[] maps;
    private int updateDelaySeconds = 10;

    public UpdateShopkeepersTask(MapData[] maps) {
        this.maps = maps;
    }

    public void execute() {
        --updateDelaySeconds;
        if (updateDelaySeconds != 0) {
            return;
        }
        updateDelaySeconds = 10;

        for (final MapData map : maps) {
            if (map != null && map.getGameInProgress() != null && map.getGameInProgress().getState() == GameState.IN_GAME) {
                update(map.getGameInProgress());
            }
        }
    }

    private void update(final GameInProgress game) {
        final long elapseSeconds = (System.currentTimeMillis() - game.getStartedTime()) / 1000;
        if (elapseSeconds % 60 == 0) {
            new ShopKeeperManager().send(game.getPlayers(), game.getWorld(), game);
        }
    }
}