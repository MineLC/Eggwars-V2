package lc.eggwars.commands.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.commands.SubCommand;
import lc.eggwars.generators.BaseGenerator;
import lc.eggwars.generators.GeneratorStorage;
import lc.eggwars.generators.SignGenerator;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.utils.IntegerUtils;

final class AddGeneratorSubCommand implements SubCommand {

    private MapCreatorData data;

    AddGeneratorSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            send(player, "&cTo use this command enable the editor mode");
            return;
        }

        if (args.length != 3) {
            send(sender, "&cFormat: /map addgenerator &7(name) (level)");
            return;
        }

        final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(args[1]);
        if (baseGenerator == null) {
            send(sender, "&cThis generator don't exist. List of generators: &e" + GeneratorStorage.getStorage().getGeneratorsName().toString());
            return;
        }

        final int level = IntegerUtils.parsePositive(args[2]);
        if (level == -1) {
            send(sender, "&cThe level need be positive");
            return;
        }
        if (level > baseGenerator.maxLevel()) {
            send(sender, "&cThe maximun level for the generator &e" + args[1] + " &cis &e" + baseGenerator.maxLevel());
            return;
        }

        final Set<Material> airBlocksStorage = null;

        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        final Material material = targetBlock.getType();

        if (material != Material.WALL_SIGN && material != Material.SIGN_POST) {
            send(player, "&cTo set a generator, you need view a sign");
            return;
        }
        final Location location = targetBlock.getLocation();
        if (creatorData.alreadyExistGenerator(location)) {
            send(sender, "&cIn this site already exist a generator. &7Use /map removegenerator");
            return;
        }
        final SignGenerator mapGenerator = new SignGenerator(location, baseGenerator, level);
        creatorData.addGenerator(mapGenerator);
        GeneratorStorage.getStorage().setGeneratorLines(targetBlock, mapGenerator);

        send(player, "&aGenerator set!");
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.copyOf(GeneratorStorage.getStorage().getGeneratorsName());
        }
        if (args.length != 3) {
            return List.of();
        }
        final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(args[1]);

        if (baseGenerator == null) {
            return List.of();
        }

        final List<String> levels = new ArrayList<>(baseGenerator.maxLevel());
        for (int i = 1; i <= baseGenerator.maxLevel(); i++) {
            levels.add(String.valueOf(i));
        }
        return levels;
    }
}