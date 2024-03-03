package lc.eggwars.others.levels;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;

public final class StartLevels {

    private final FileConfiguration config;

    public StartLevels(EggwarsPlugin plugin) {
        config = plugin.loadConfig("levels");
    }

    public void load() {
        LevelStorage.update(new LevelStorage(
            createStat("kills"),
            createStat("deaths"), 
            createStat("final-kills"), 
            createStat("final-deaths"), 
            createStat("wins")));
    }

    private LevelStat createStat(final String path) {
        return new LevelStat(
            config.getInt(path + ".every"),
            config.getInt(path + ".level-up")
        );
    }
}