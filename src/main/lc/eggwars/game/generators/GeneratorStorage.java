package lc.eggwars.game.generators;

import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import net.md_5.bungee.api.ChatColor;

public final class GeneratorStorage {
    private static GeneratorStorage storage;

    private final Map<String, BaseGenerator> generatorsPerName;
    private final String[] lines;

    GeneratorStorage(Map<String, BaseGenerator> generatorsPerName, String[] lines) {
        this.generatorsPerName = generatorsPerName;
        this.lines = lines;
    }

    public void setLines(final Block block, final BaseGenerator generator, final int level) {
        if (!(block.getState() instanceof Sign sign)) {
            return;
        }
        final BaseGenerator.Level genLevel = generator.levels()[level];
        final String levelString = String.valueOf(level);
        final String currentProgress = (genLevel.itemsToGenerate() <= 0)
            ? ChatColor.RED + "X" : String.valueOf(genLevel.percentage());

        final String progress = currentProgress + "-" + generator.levels()[generator.maxlevel()].percentage() + "%";

        final String speed = genLevel.itemsToGenerate() + "/" + genLevel.waitingTime() + "s";
        final String speedNext = (level == generator.maxlevel())
            ? ChatColor.BOLD + "MAX"
            : generator.levels()[level + 1].itemsToGenerate() + "/" + generator.levels()[level + 1].waitingTime() + "s";

        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]
                .replace("%name%", generator.name())
                .replace("%level%", levelString)
                .replace("%progress%", progress)
                .replace("%speed%", speed)
                .replace("%speed_next%", speedNext));
        }

        sign.update();
    }

    public Set<String> getGeneratorsName() {
        return this.generatorsPerName.keySet();
    }

    public BaseGenerator getGenerator(final String name) {
        return generatorsPerName.get(name);
    }

    public static GeneratorStorage getStorage() {
        return storage;
    }

    static final void update(GeneratorStorage newStorage) {
        storage = newStorage;
    }
}