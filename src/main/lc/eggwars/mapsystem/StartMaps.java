package lc.eggwars.mapsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.EggwarsPlugin;
import lc.eggwars.generators.BaseGenerator;
import lc.eggwars.generators.GeneratorStorage;
import lc.eggwars.generators.GeneratorThread;
import lc.eggwars.generators.SignGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.IntegerUtils;
import net.minecraft.server.v1_8_R3.EntityItem;

public final class StartMaps {

    private final EggwarsPlugin plugin;

    public StartMaps(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load(SlimePlugin slimePlugin) {
        final SlimeLoader loader = slimePlugin.getLoader("file");
        final File mapFolder = new File(plugin.getDataFolder(), "maps");

        if (!mapFolder.exists()) {
            mapFolder.mkdir();
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>()));
            return;
        }

        final File[] mapFiles = mapFolder.listFiles();
        if (mapFiles == null) {
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>()));
            return;
        }

        final Gson gson = new Gson();
        final Map<String, GameMap> mapsPerName = new HashMap<>();
        final GameMap[] maps = new GameMap[mapFiles.length];
        int index = 0;
        int id = -1;

        for (final File mapFile : mapFiles) {
            if (!mapFile.getName().endsWith(".json")) {
                continue;
            }
            try {
                final IntObjectHashMap<ClickableBlock> worldClickableBlocks = new IntObjectHashMap<>();
                final JsonMapData data = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(mapFile))), JsonMapData.class);
                final World world = Bukkit.getWorld(data.world());

                if (world == null) {
                    plugin.getLogger().warning("The world " + data.world() + " don't exist or is unloaded");
                    continue;
                }

                setTeamEggs(data, worldClickableBlocks);

                final GameMap map = new GameMap(
                    worldClickableBlocks,
                    getGenerators(data, worldClickableBlocks),
                    getSpawns(data),
                    data.borderSize(),
                    ++id);

                maps[index++] = map;

                mapsPerName.put(world.getName(), map);
                plugin.getServer().getScheduler().runTask(plugin, () -> Bukkit.unloadWorld(world, true));
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                plugin.getLogger().warning("Error on load the map: " + mapFile.getName() + ". Check the json in: " + mapFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
        MapStorage.update(new MapStorage(slimePlugin, loader, mapsPerName));

        final GeneratorThread thread = new GeneratorThread(maps);
        GeneratorThread.setThread(thread);
        thread.start();
    }

    private void setTeamEggs(final JsonMapData data, final IntObjectHashMap<ClickableBlock> clickableBlocks) {
        final Set<Entry<String, String>> eggsEntryEntries = data.teamEggs().entrySet();
        for (final Entry<String, String> entry : eggsEntryEntries) {
            final BaseTeam team = TeamStorage.getStorage().getTeam(entry.getKey());

            if (team == null) {
                plugin.getLogger().warning("The map " + data.world() + " uses a inexistent team: " + entry.getKey());
                continue;
            }

            final BlockLocation location = BlockLocation.create(entry.getValue());
            clickableBlocks.put(location.hashCode(), new EnderDragonEgg(team, location));
        }
    }

    private Map<BaseTeam, BlockLocation> getSpawns(final JsonMapData data) {
        final Map<BaseTeam, BlockLocation> spawnsParsed = new HashMap<>();
        final Set<Entry<String, String>> spawns = data.spawns().entrySet();
            
        for (final Entry<String, String> spawn : spawns) {
            final BaseTeam team = TeamStorage.getStorage().getTeam(spawn.getKey());

            if (team == null) {
                plugin.getLogger().warning("The map " + data.world() + " uses a inexistent team: " + spawn.getKey());
                continue;
            }

            spawnsParsed.put(team, BlockLocation.create(spawn.getValue()));
        }
        return spawnsParsed;
    }
    
    private SignGenerator[] getGenerators(final JsonMapData data, final IntObjectHashMap<ClickableBlock> clickableBlocks) {
        final Set<Entry<String, String[]>> generatorsEntries = data.generators().entrySet();

        int size = 0;

        for (final Entry<String, String[]> generator : generatorsEntries) {
            size += generator.getValue().length;
        }

        final SignGenerator[] maps = new SignGenerator[size];
        int mapIndex = 0;

        for (final Entry<String, String[]> generator : generatorsEntries) {
            final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(generator.getKey());
            if (baseGenerator == null) {
                plugin.getLogger().warning("The generator " + generator.getKey() + " don't exist");
                return null;
            }
            final String[] cordsAndLevels = generator.getValue();

            for (final String generatorString : cordsAndLevels) {
                final String[] split = generatorString.split(":");
                final int level = IntegerUtils.parsePositive(split[0]);
                final BlockLocation location = BlockLocation.create(split[1]);

                final EntityItem entityItem = new EntityItem(null);

                entityItem.setCustomNameVisible(true);
                entityItem.setItemStack(baseGenerator.item());

                entityItem.locX = location.x();
                entityItem.locY = location.y();
                entityItem.locZ = location.z();

                final SignGenerator mapGenerator = new SignGenerator(location, baseGenerator, entityItem, level);

                maps[mapIndex++] = mapGenerator;
                clickableBlocks.put(location.hashCode(), mapGenerator);
            }
        }

        return maps;
    }
}