package lc.eggwars.game.generators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.tinylog.Logger;

import lc.eggwars.EggwarsPlugin;
import net.md_5.bungee.api.ChatColor;

import net.minecraft.server.v1_8_R3.ItemStack;

public final class StartGenerators {

    public void load(final EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("generators");
        final Set<String> generators = config.getKeys(false);
        final Map<String, BaseGenerator> generatorsPerName = new HashMap<>();

        for (final String generator : generators) {
            final String generatorPath = generator + '.';

            Material drop = Material.getMaterial(config.getString(generatorPath + "drop"));
            if (drop == null) {
                Logger.warn("The item to generate is invalid. Generator: " + generator);
                drop = Material.STONE;
            }

            final ItemStack item = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(drop));
            final int maxlevel = config.getInt(generatorPath + "max-level");

            generatorsPerName.put(generator, new BaseGenerator(
                generator,
                config.getString(generatorPath + "name").replace('&', ChatColor.COLOR_CHAR),
                item,
                maxlevel,
                getLevels(config, generatorPath, maxlevel + 1)));  
        }
        GeneratorStorage.update(new GeneratorStorage(generatorsPerName, getSignLines(plugin.getConfig())));
    }

    private BaseGenerator.Level[] getLevels(final FileConfiguration config, final String generatorPath, final int amountLevels) {
        final BaseGenerator.Level[] levels = new BaseGenerator.Level[amountLevels];

        final int startLevel = config.getInt(generatorPath + "start-level");
        float itemsPerSecondFirstLevel = 0;

        for (int level = 0; level < amountLevels; level++) {
            final String levelPath = generatorPath + "level." + level + ".";

            final int waitingTime = config.getInt(levelPath + "seconds-to-generate");
            final int amountToGenerate = config.getInt(levelPath + "amount-generate");

            final float itemsPerSecond = (float)amountToGenerate / (float)waitingTime;
            int percentage;
            if (startLevel == level) {
                percentage = 0;
                itemsPerSecondFirstLevel = itemsPerSecond;
            } else {
                percentage = (int) (((itemsPerSecond / itemsPerSecondFirstLevel) - 1.0F) * 100.0F);
            }
            levels[level] = new BaseGenerator.Level(
                config.getInt(levelPath + "upgrade-item-need"),
                waitingTime,
                amountToGenerate,
                percentage,
                config.getInt(levelPath + "refresh-every"));            
        }
        return levels;
    }

    private String[] getSignLines(final FileConfiguration config) {
        final List<String> lines = config.getStringList("sign-lines");
        final int size = (lines.size() > 4) ? 4 : lines.size();
        final String[] parsedLines = new String[size];

        for (int i = 0; i < size; i++) {
            parsedLines[i] = lines.get(i).replace('&', ChatColor.COLOR_CHAR);
        }
        return parsedLines;
    }
}
