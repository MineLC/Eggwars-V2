package lc.eggwars.commands.map;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.io.Files;
import com.google.gson.Gson;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.commands.BasicSubCommand;
import lc.eggwars.generators.GeneratorData;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.JsonMapData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.spawn.SpawnStorage;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

final class SaveSubCommand implements BasicSubCommand {

    private final EggwarsPlugin plugin;
    private final MapCreatorData data;

    SaveSubCommand(EggwarsPlugin plugin, MapCreatorData data) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public void execute(Player player, String[] args) {
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            send(player, "&cTo use this command enable the editor mode");
            return;
        }
        if (args.length != 2) {
            send(player, "&cFormat: /map save &7(mapname)");
            return;
        }
        final File mapFolder = new File(plugin.getDataFolder(), "maps");
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
        }
        final File mapFile = new File(mapFolder, args[1] + ".json");
        if (mapFile.exists()) {
            send(player, "&cAlready exist a map with this name. Try other");
            return;
        }

        try {
            mapFile.createNewFile();

            data.remove(player.getUniqueId());
            send(player, "&aMap saved in: " + mapFile.getPath());

            final JsonMapData object = saveMapInfo(creatorData, player.getWorld());
            Files.write(new Gson().toJson(object), mapFile, Charset.forName("UTF-8"));

            player.teleport(SpawnStorage.getStorage().getLocation());
            plugin.getServer().getScheduler().runTask(plugin, () -> Bukkit.unloadWorld(player.getWorld(), true));
        } catch (IOException e) {
            send(player, "&cError on create the map");
            e.printStackTrace();
            return;
        }
    }

    private JsonMapData saveMapInfo(final CreatorData data, final World world) {
        return new JsonMapData(
            world.getName(),
            4,
            (int)world.getWorldBorder().getSize(),
            saveSpawns(data),
            saveGenerators(data),
            saveEggs(data)
        );
    }

    private Map<String, String> saveSpawns(final CreatorData data) {
        final Set<Entry<BaseTeam, BlockLocation>> spawnData = data.getSpawnsMap().entrySet();
        final Map<String, String> spawns = new HashMap<>();

        for (final Entry<BaseTeam, BlockLocation> entry : spawnData) {
            spawns.put(entry.getKey().getKey(), entry.getValue().toString());
        }
        return spawns;
    }

    private Map<String, String[]> saveGenerators(final CreatorData data) {
        final Set<Entry<String, List<GeneratorData>>> generatorsData = data.getGeneratorsMapPerID().entrySet();
        final Map<String, String[]> generatorsParsed = new HashMap<>();

        for (final Entry<String, List<GeneratorData>> entry : generatorsData) {
            final List<GeneratorData> list = entry.getValue();
            final String[] generatorsString = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                final GeneratorData generator = list.get(i);
                generatorsString[i] = generator.getDefaultLevel() + ":" + generator.getLocation().toString();
            }

            generatorsParsed.put(entry.getKey(), generatorsString);
        }
        return generatorsParsed;
    }

    private Map<String, String> saveEggs(final CreatorData data) {
        final Set<Entry<BaseTeam, BlockLocation>> spawnData = data.getEggsMap().entrySet();
        final Map<String, String> eggs = new HashMap<>();

        for (final Entry<BaseTeam, BlockLocation> entry : spawnData) {
            eggs.put(entry.getKey().getKey(), entry.getValue().toString());
        }
        return eggs;
    }
}