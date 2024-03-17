package lc.eggwars.game.threadtasks;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.mapsystem.MapData;

public final class UpdateShopkeepersTask {

    private final MapData[] maps;

    public UpdateShopkeepersTask(MapData[] maps) {
        this.maps = maps;
    }

    public void execute() {
        for (final MapData map : maps) {
            if (map != null && map.getGameInProgress() != null && map.getGameInProgress().getState() == GameState.IN_GAME) {
                update(map.getGameInProgress());
            }
        }
    }

    private void update(final GameInProgress game) {
        final long elapseSeconds = (System.currentTimeMillis() - game.getStartedTime()) / 1000;
        if (elapseSeconds % 30 == 0) {
            game.getPlayers().forEach((player) -> player.sendMessage("ENVIADO PAPU. ELAPSE: " + elapseSeconds));
            new ShopKeeperManager().send(game.getPlayers(), game.getWorld(), game);
        }
    }
}