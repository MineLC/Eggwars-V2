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
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.EggwarsPlugin;
import lc.eggwars.generators.BaseGenerator;
import lc.eggwars.generators.GeneratorStorage;
import lc.eggwars.generators.SignGenerator;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.ClickableBlock;
import lc.eggwars.utils.IntegerUtils;

public final class StartMaps {

    private final EggwarsPlugin plugin;

    public StartMaps(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final File mapFolder = new File(plugin.getDataFolder(), "maps");
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
            MapStorage.update(new MapStorage(new HashMap<>(), null));
            return;
        }

        final File[] mapFiles = mapFolder.listFiles();
        if (mapFiles == null) {
            MapStorage.update(new MapStorage(new HashMap<>(), null));
            return;
        }

        final Gson gson = new Gson();
        final Map<World, IntObjectHashMap<ClickableBlock>> clickableBlocks = new HashMap<>();
        final GameMap[] maps = new GameMap[mapFiles.length];
        int mapIndex = -1;

        for (final File mapFile : mapFiles) {
            if (!mapFile.getName().endsWith(".json")) {
                continue;
            }
            try {
                final IntObjectHashMap<ClickableBlock> worldClickableBlocks = new IntObjectHashMap<>();
                final JsonMapData data = gson.fromJson(new BufferedReader(new FileReader(mapFile)), JsonMapData.class);
                final World world = Bukkit.getWorld(data.getWorldName());

                if (world == null) {
                    plugin.getLogger().warning("The world " + data.getWorldName() + " don't exist or is unloaded");
                    continue;
                }

                maps[++mapIndex] = new GameMap(
                    getGenerators(world, data, worldClickableBlocks),
                    getSpawns(world, data));

                if (!worldClickableBlocks.isEmpty()) {
                    clickableBlocks.put(world, worldClickableBlocks);
                }
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                plugin.getLogger().warning("Error on load the map: " + mapFile.getName() + ". Check the json in: " + mapFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
        MapStorage.update(new MapStorage(clickableBlocks, maps));
    }


    private Map<BaseTeam, Location[]> getSpawns(final World world, final JsonMapData data) {
        final Map<BaseTeam, Location[]> spawnsParsed = new HashMap<>();
        final Set<Entry<String, String[]>> spawns = data.getSpawns().entrySet();
            
        for (final Entry<String, String[]> spawn : spawns) {
            final BaseTeam team = TeamStorage.getStorage().getTeam(spawn.getKey());

            if (team == null) {
                plugin.getLogger().warning("The map " + data.getWorldName() + " uses a inexistent team: " + spawn.getKey());
                continue;
            }
            final Location[] locations = new Location[spawn.getValue().length];
            int locationsIndex = -1;

            for (final String locationString : spawn.getValue()) {
                locations[++locationsIndex] = parseToLocation(world, locationString);
            }

            spawnsParsed.put(team, locations);
        }
        return spawnsParsed;
    }
    
    private SignGenerator[] getGenerators(final World world, final JsonMapData data, final IntObjectHashMap<ClickableBlock> clickableBlocks) {
        final Set<Entry<String, String[]>> generatorsEntries = data.getGenerators().entrySet();

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
                final Location location = parseToLocation(world, split[1]);
                final SignGenerator mapGenerator = new SignGenerator(location, baseGenerator, level);

                maps[mapIndex++] = mapGenerator;
                clickableBlocks.put(
                    IntegerUtils.combineCords(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                    mapGenerator);
            }
        }

        return maps;
    }


    private Location parseToLocation(final World world, final String text) {
        final String[] split = text.split(",");
        return new Location(
            world,
            Integer.parseInt(split[0]),
            Integer.parseInt(split[1]),
            Integer.parseInt(split[2]));
    }
}