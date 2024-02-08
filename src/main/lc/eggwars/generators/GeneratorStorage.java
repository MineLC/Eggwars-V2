package lc.eggwars.generators;

import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public final class GeneratorStorage {
    private static GeneratorStorage generatorStorage;

    private final Map<String, BaseGenerator> generators;
    private final String[] signLines;

    GeneratorStorage(Map<String, BaseGenerator> generators, String[] signLines) {
        this.generators = generators;
        this.signLines = signLines;
    }

    public BaseGenerator getGenerator(final String name) {
        return generators.get(name);
    }

    public Set<String> getGeneratorsName() {
        return generators.keySet();
    }

    public void setGeneratorLines(final Block block, final SignGenerator generator) {
        if (!(block.getState() instanceof Sign sign)) {
            return;
        }
        for (int i = 0; i < signLines.length; i++) {
            if (signLines[i].isEmpty()) {
                continue;
            }
            final BaseGenerator.Level level = generator.getBase().levels()[generator.getLevel()];
            sign.setLine(i, signLines[i]
                .replace("%name%", generator.getBase().name())
                .replace("%level%", String.valueOf(generator.getLevel()))
                .replace("%speed%", String.valueOf(level.secondsToGenerate()))
                .replace("%amount%", String.valueOf(level.amountGenerated())));
        }
        sign.update();
    }

    public static GeneratorStorage getStorage() {
        return generatorStorage;
    }

    final static void update(final GeneratorStorage newGeneratorStorage) {
        generatorStorage = newGeneratorStorage;
    }
}