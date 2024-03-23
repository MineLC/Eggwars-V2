package lc.eggwars.game;


import org.tinylog.Logger;

import lc.eggwars.game.threadtasks.GeneratorTask;
import lc.eggwars.game.threadtasks.UpdateShopkeepersTask;
import lc.eggwars.mapsystem.MapData;

public final class GameManagerThread extends Thread {

    private static final GameManagerThread THREAD = new GameManagerThread();

    private GeneratorTask generatorTask;
    private UpdateShopkeepersTask updateShopkeepersTask;

    private boolean run = false;

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(1000);

                generatorTask.execute();
                updateShopkeepersTask.execute();

            } catch (Exception e) {
                Logger.error(e);
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
        THREAD.generatorTask = new GeneratorTask(maps);
        THREAD.updateShopkeepersTask = new UpdateShopkeepersTask(maps);
    }
}