package lc.eggwars.game;


import org.tinylog.Logger;

import lc.eggwars.game.threadtasks.EventTask;
import lc.eggwars.game.threadtasks.GeneratorTask;
import lc.eggwars.game.threadtasks.UpdateShopkeepersTask;
import lc.eggwars.mapsystem.MapData;

public final class GameManagerThread extends Thread {

    private static final GameManagerThread THREAD = new GameManagerThread();

    private static final GeneratorTask GENERATOR_TASK = new GeneratorTask();
    private static final UpdateShopkeepersTask UPDATE_SHOPKEEPERS_TASK = new UpdateShopkeepersTask();
    private static final EventTask EVENT_TASK = new EventTask();

    private boolean run = false;
    private MapData[] maps;

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(1000);
                executeTasks();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }

    private void executeTasks() {
        final long time = System.currentTimeMillis();

        for (final MapData map : maps) {
            if (map != null && map.getGameInProgress() != null) {
                GENERATOR_TASK.execute(map);
                UPDATE_SHOPKEEPERS_TASK.execute(map, time);
                EVENT_TASK.execute(map, time);
            }
        }
    }

    public static void startThread() {
        if (!THREAD.run) {
            THREAD.run = true;
            THREAD.start();
        }
    }

    public static void stopThread() {
        THREAD.run = false;
    }

    public static void setMaps(final MapData[] maps) {
        THREAD.maps = maps;
    }
}