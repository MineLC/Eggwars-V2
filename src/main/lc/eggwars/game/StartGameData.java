package lc.eggwars.game;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.countdown.types.PreGameCountdown;
import lc.eggwars.messages.Messages;

public class StartGameData {

    public void load(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("countdowns");
        final PreGameCountdown.Data preGameData = new PreGameCountdown.Data(
            config.getInt("waiting-to-start"),
            config.getInt("game-starting-in"),
            config.getInt("sound-starting"),
            config.getInt("spam-message"),
            config.getInt("minimum-players-to-start"),
            Messages.get("pregame.waiting-players"),
            Messages.get("pregame.start-game"),
            Messages.get("pregame.start-in"));

        GameStorage.update(new GameStorage(plugin, preGameData));
    }
}