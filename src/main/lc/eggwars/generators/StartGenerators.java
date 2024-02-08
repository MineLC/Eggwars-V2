package lc.eggwars.generators;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import lc.eggwars.EggwarsPlugin;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StartGenerators {

    private final EggwarsPlugin plugin;

    public StartGenerators(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final FileConfiguration config = plugin.loadConfig("generators");
        final Set<String> generators = config.getKeys(false);
        final BaseGenerator[] baseGenerators = new BaseGenerator[generators.size()];
        final Map<String, BaseGenerator> generatorsByName = new HashMap<>();

        int generatorsIndex = -1;

        for (final String generator : generators) {
            final String path = generator + '.';
    
            Material material = Material.getMaterial(config.getString(path + ".drop"));
            if (material == null) {
                material = Material.STONE;
                plugin.getLogger().warning("Error on get the drop material of the generator: " + generator);
            }

            final int maxLevel = config.getInt(path + "max-level");
            final String name = config.getString(path + ".sign-name");

            final GeneratorDropitem drop = new GeneratorDropitem();
            final net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(new ItemStack(material));

            drop.setCustomNameVisible(true);
            drop.setItemStack(item);

            final BaseGenerator baseGenerator = new BaseGenerator(
                generator,
                (name == null) ? generator : name.replace('&', ChatColor.COLOR_CHAR),
                getLevels(config, path + "level.", maxLevel),
                item,
                drop,
                maxLevel
            );

            baseGenerators[++generatorsIndex] = baseGenerator;
            generatorsByName.put(generator, baseGenerator);
        }

        GeneratorStorage.update(new GeneratorStorage(generatorsByName, getSignLines()));
    }

    private BaseGenerator.Level[] getLevels(final FileConfiguration config, final String levelPath, final int maxLevel) {
        final BaseGenerator.Level[] levels = new BaseGenerator.Level[maxLevel + 1];

        for (int i = 0; i < maxLevel; i++) {
            final String path = levelPath + i;
            levels[i] = new BaseGenerator.Level(
                config.getInt(path + ".upgrade-item-need"),
                config.getInt(path + ".seconds-to-generate"),
                config.getInt(path + ".amount-generate"));
        }
        return levels;
    }

    private String[] getSignLines() {
        final List<String> unparsedLines = plugin.getConfig().getStringList("sign-lines");
        final int size = (unparsedLines.size() > 4) ? 4 : unparsedLines.size();

        final String[] parsedLines = new String[size];

        for (int i = 0; i < size; i++) {
            parsedLines[i] = unparsedLines.get(i).replace('&', ChatColor.COLOR_CHAR);
        }
        return parsedLines;
    }
}