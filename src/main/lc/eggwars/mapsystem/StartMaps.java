package lc.eggwars.mapsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.plugin.SWMPlugin;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameManagerThread;
import lc.eggwars.game.clickable.ClickableDragonEgg;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.BaseGenerator;
import lc.eggwars.game.generators.GeneratorStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.EntityLocation;
import lc.eggwars.utils.IntegerUtils;

public final class StartMaps {

    private final EggwarsPlugin plugin;
    private final SlimeLoader loader;
    private final SWMPlugin slimePlugin;

    public StartMaps(EggwarsPlugin plugin, SWMPlugin slimePlugin) {
        this.plugin = plugin;
        this.slimePlugin = slimePlugin;
        this.loader = slimePlugin.getLoader("file");
    }

    public void load() {
        final File mapFolder = new File(plugin.getDataFolder(), "maps");

        if (!mapFolder.exists()) {
            mapFolder.mkdir();
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>(), new MapData[0], new MapData[0]));
            return;
        }

        final File[] mapFiles = mapFolder.listFiles();
        if (mapFiles == null) {
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>(), new MapData[0], new MapData[0]));
            return;
        }
        final Map<String, MapData> mapsPerName = new HashMap<>();
        final MapData[] maps = new MapData[mapFiles.length];
        if (mapFiles.length > 0) {
            loadMapData(maps, mapFiles, mapsPerName);
        }
    
        final List<MapData> soloMaps = new ArrayList<>();
        final List<MapData> teamMaps = new ArrayList<>();
        for (final MapData map : maps) {
            if (map.getMaxPersonsPerTeam() > 1) {
                teamMaps.add(map);
                continue;
            }
            soloMaps.add(map);
        }
    
        MapStorage.update(new MapStorage(slimePlugin, loader, mapsPerName, soloMaps.toArray(new MapData[0]), teamMaps.toArray(new MapData[0])));
        GameManagerThread.setMaps(maps);
    }

    private void loadMapData(final MapData[] maps, final File[] mapFiles, final Map<String, MapData> mapsPerName) {
        final Gson gson = new Gson();
        int index = 0;
        final List<File> ordenedFiles = new ArrayList<>();
        for (final File file : mapFiles) {
            ordenedFiles.add(file);
        }
        Collections.sort(ordenedFiles, new Comparator<File>() {
            @Override
            public int compare(File object1, File object2) {
                final int value1 = object1.getName().charAt(0) - '0';
                final int value2 = object2.getName().charAt(0) - '0';
                return (value1 == value2) ? 0 : (value1 > value2) ? 1 : -1;
            }
        });
        for (final File mapFile : ordenedFiles) {
            if (!mapFile.getName().endsWith(".json")) {
                continue;
            }
            try {
                final JsonMapData data = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(mapFile))), JsonMapData.class);
                final MapData map = loadMapData(data, index);

                maps[index] = map;
                mapsPerName.put(data.world(), map);
                index++;
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                Logger.error("Error on load the map: " + mapFile.getName() + ". Check the json in: " + mapFile.getAbsolutePath());
                Logger.error(e);
            }
        }
    }

    private MapData loadMapData(final JsonMapData data, final int id) {
        final IntObjectHashMap<ClickableBlock> worldClickableBlocks = new IntObjectHashMap<>();
        final EntityLocation[] shopSpawns = getShopSpawns(data);

        final MapData map = new MapData(
            worldClickableBlocks,
            getSpawns(data),
            getTeamEggs(data, worldClickableBlocks),
            getGenerators(data, worldClickableBlocks),
            shopSpawns,
            data.maxPersonsPerTeam(),
            data.borderSize(),
            id,
            data.world(),
            Messages.color(data.name())
        );
        return map;
    }

    private Map<BaseTeam, BlockLocation> getTeamEggs(final JsonMapData data, final IntObjectHashMap<ClickableBlock> clickableBlocks) {
        final Map<BaseTeam, BlockLocation> eggsParsed = new HashMap<>();
        final Set<Entry<String, String>> eggsEntryEntries = data.teamEggs().entrySet();

        for (final Entry<String, String> entry : eggsEntryEntries) {
            final BaseTeam team = TeamStorage.getStorage().getTeam(entry.getKey());

            if (team == null) {
                Logger.info("The map " + data.world() + " uses a inexistent team: " + entry.getKey());
                continue;
            }

            final BlockLocation location = BlockLocation.create(entry.getValue());
            eggsParsed.put(team, location);
            clickableBlocks.put(location.hashCode(), new ClickableDragonEgg(team, location));
        }

        return eggsParsed;
    }

    private EntityLocation[] getShopSpawns(final JsonMapData data) {
        final String[] locations = data.shopspawns();
        final EntityLocation[] parsedLocations = new EntityLocation[locations.length];
        int index = 0;
        for (final String location : locations) {
            final EntityLocation entityLocation = EntityLocation.create(location);
            parsedLocations[index++] = entityLocation;
        }
        return parsedLocations;
    }

    private Map<BaseTeam, BlockLocation> getSpawns(final JsonMapData data) {
        final Map<BaseTeam, BlockLocation> spawnsParsed = new HashMap<>();
        final Set<Entry<String, String>> spawns = data.spawns().entrySet();
            
        for (final Entry<String, String> spawn : spawns) {
            final BaseTeam team = TeamStorage.getStorage().getTeam(spawn.getKey());

            if (team == null) {
                Logger.info("The map " + data.world() + " uses a inexistent team: " + spawn.getKey());
                continue;
            }

            spawnsParsed.put(team, BlockLocation.create(spawn.getValue()));
        }
        return spawnsParsed;
    }
    
    private ClickableSignGenerator[] getGenerators(final JsonMapData data, final IntObjectHashMap<ClickableBlock> clickableBlocks) {
        final Set<Entry<String, String[]>> generatorsEntries = data.generators().entrySet();

        int size = 0;

        for (final Entry<String, String[]> generator : generatorsEntries) {
            size += generator.getValue().length;
        }
        final int pickupDistance = plugin.getConfig().getInt("generators.pickup-distance-blocks");
        final int viewDistance = plugin.getConfig().getInt("generators.view-distance-blocks");
        final ClickableSignGenerator[] generatorsData = new ClickableSignGenerator[size];
        int index = 0;

        for (final Entry<String, String[]> generator : generatorsEntries) {
            final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(generator.getKey());
            if (baseGenerator == null) {
                Logger.info("The generator " + generator.getKey() + " don't exist");
                return null;
            }
            final String[] cordsAndLevels = generator.getValue();

            for (final String generatorString : cordsAndLevels) {
                final String[] split = StringUtils.split(generatorString, ':');
                final int defaultLevel = IntegerUtils.parsePositive(split[0]);
                final BlockLocation location = BlockLocation.create(split[1]);
                
                final BlockLocation pickMin = new BlockLocation(location.x() - pickupDistance, location.y() - 1, location.z() - pickupDistance);
                final BlockLocation pickMax = new BlockLocation(location.x() + pickupDistance, location.y() + 1, location.z() + pickupDistance);
                final BlockLocation viewMin = new BlockLocation(location.x() - viewDistance, location.y() - viewDistance, location.z() - viewDistance);
                final BlockLocation viewMax = new BlockLocation(location.x() + viewDistance, location.y() + viewDistance, location.z() + viewDistance);

                final ClickableSignGenerator generatorData = new ClickableSignGenerator(location, pickMin, pickMax, viewMin, viewMax, defaultLevel, baseGenerator);

                generatorsData[index++] = generatorData;
                clickableBlocks.put(location.hashCode(), generatorData);
            }
        }

        return generatorsData;
    }
}