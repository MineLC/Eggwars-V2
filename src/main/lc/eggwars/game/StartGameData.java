package lc.eggwars.game;

import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.types.PreGameCountdown;

public class StartGameData {

    public void load(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("countdowns");
        final PreGameCountdown.Data preGameData = new PreGameCountdown.Data(
            config.getInt("waiting-to-start"),
            config.getInt("game-starting-in"),
            config.getInt("sound-starting"),
            config.getInt("spam-message"),
            config.getInt("minimum-players-to-start"));

        GameStorage.update(new GameStorage(plugin, preGameData, new HashMap<>()));
    }
}