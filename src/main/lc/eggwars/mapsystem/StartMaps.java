package lc.eggwars.mapsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import io.netty.util.collection.IntObjectHashMap;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.clickable.ClickableDragonEgg;
import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.BaseGenerator;
import lc.eggwars.game.generators.GeneratorStorage;
import lc.eggwars.game.generators.GeneratorThread;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.EntityLocation;
import lc.eggwars.utils.IntegerUtils;

import net.swofty.swm.api.SlimePlugin;
import net.swofty.swm.api.loaders.SlimeLoader;

public final class StartMaps {

    private final EggwarsPlugin plugin;

    public StartMaps(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<?> load(SlimePlugin slimePlugin) {
        final SlimeLoader loader = slimePlugin.getLoader("file");
        final File mapFolder = new File(plugin.getDataFolder(), "maps");

        if (!mapFolder.exists()) {
            mapFolder.mkdir();
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>()));
            return null;
        }

        final File[] mapFiles = mapFolder.listFiles();
        if (mapFiles == null) {
            MapStorage.update(new MapStorage(slimePlugin, loader, new HashMap<>()));
            return null;
        }

        return CompletableFuture.runAsync( () -> {
            final Map<String, MapData> mapsPerName = new HashMap<>();
            final MapData[] maps = new MapData[mapFiles.length];

            if (mapFiles.length != 0) {
                loadMapData(maps, mapFiles, new HashMap<>());
            }
            MapStorage.update(new MapStorage(slimePlugin, loader, mapsPerName));

            final GeneratorThread thread = new GeneratorThread(maps);
            GeneratorThread.setThread(thread);
            thread.start();
        });
    }

    private void loadMapData(final MapData[] maps, final File[] mapFiles, final Map<String, MapData> mapsPerName) {
        final Gson gson = new Gson();
        int index = 0;
        int id = -1;
        int shopsAmount = 0;
        for (final File mapFile : mapFiles) {
            if (!mapFile.getName().endsWith(".json")) {
                continue;
            }
            try {
                final IntObjectHashMap<ClickableBlock> worldClickableBlocks = new IntObjectHashMap<>();
                final JsonMapData data = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(mapFile))), JsonMapData.class);

                if (MapStorage.getStorage().loadNoGameMap(data.world()) == null) {
                    continue;
                }
                final World world = Bukkit.getWorld(data.world());
    
                final EntityLocation[] shopSpawns = getShopSpawns(data);
                final int[] shopkeepersID = new int[shopSpawns.length];
                for (int i = 0; i < shopSpawns.length; i++) {
                    shopkeepersID[i] = Integer.MAX_VALUE - shopsAmount;
                }

                final MapData map = new MapData(
                    worldClickableBlocks,
                    getSpawns(data),
                    getTeamEggs(data, worldClickableBlocks),
                    getGenerators(data, worldClickableBlocks),
                    shopSpawns,
                    shopkeepersID,
                    data.maxPersonsPerTeam(),
                    data.borderSize(),
                    ++id);

                maps[index++] = map;

                mapsPerName.put(data.world(), map);
                plugin.getServer().getScheduler().runTask(plugin, () -> Bukkit.unloadWorld(world, false));
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                Logger.warn("Error on load the map: " + mapFile.getName() + ". Check the json in: " + mapFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    private Map<BaseTeam, BlockLocation> getTeamEggs(final JsonMapData data, final IntObjectHashMap<ClickableBlock> clickableBlocks) {
        final Map<BaseTeam, BlockLocation> eggsParsed = new HashMap<>();
        final Set<Entry<String, String>> eggsEntryEntries = data.teamEggs().entrySet();

        for (final Entry<String, String> entry : eggsEntryEntries) {
            final BaseTeam team = TeamStorage.getStorage().getTeam(entry.getKey());

            if (team == null) {
                Logger.warn("The map " + data.world() + " uses a inexistent team: " + entry.getKey());
                continue;
            }

            final BlockLocation location = BlockLocation.create(entry.getValue());
            eggsParsed.put(team, location);
            clickableBlocks.put(location.hashCode(), new ClickableDragonEgg(team, location));
        }

        return eggsParsed;
    }

    private EntityLocation[] getShopSpawns(final JsonMapData data) {
        final String[] locations = data.shopSpawns();
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
                Logger.warn("The map " + data.world() + " uses a inexistent team: " + spawn.getKey());
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

        final ClickableSignGenerator[] generatorsData = new ClickableSignGenerator[size];
        int index = 0;

        for (final Entry<String, String[]> generator : generatorsEntries) {
            final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(generator.getKey());
            if (baseGenerator == null) {
                Logger.warn("The generator " + generator.getKey() + " don't exist");
                return null;
            }
            final String[] cordsAndLevels = generator.getValue();

            for (final String generatorString : cordsAndLevels) {
                final String[] split = generatorString.split(":");
                final int defaultLevel = IntegerUtils.parsePositive(split[0]);
                final BlockLocation location = BlockLocation.create(split[1]);
                final ClickableSignGenerator generatorData = new ClickableSignGenerator(location, defaultLevel, baseGenerator);

                generatorsData[index++] = generatorData;
                clickableBlocks.put(location.hashCode(), generatorData);
            }
        }

        return generatorsData;
    }
}