package lc.eggwars.commands.map;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.game.generators.BaseGenerator;
import lc.eggwars.game.generators.GeneratorStorage;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.IntegerUtils;

final class AddGeneratorSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        if (args.length != 3) {
            sendWithColor(player, "&cFormat: /map addgenerator &7(name) (level)");
            return;
        }

        final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(args[1]);
        if (baseGenerator == null) {
            sendWithColor(player, "&cThis generator don't exist. List of generators: &e" + GeneratorStorage.getStorage().getGeneratorsName().toString());
            return;
        }

        final int level = IntegerUtils.parsePositive(args[2]);
        if (level == -1) {
            sendWithColor(player, "&cThe level need be positive");
            return;
        }
        if (level > baseGenerator.maxlevel()) {
            sendWithColor(player, "&cThe max level for the generator &e" + args[1] + " &cis &e" + baseGenerator.maxlevel());
            return;
        }

        final Set<Material> airBlocksStorage = null;

        final Block targetBlock = player.getTargetBlock(airBlocksStorage, 3);
        final Material material = targetBlock.getType();

        if (material != Material.WALL_SIGN && material != Material.SIGN_POST) {
            sendWithColor(player, "&cTo set a generator, you need view a sign");
            return;
        }
        final BlockLocation location = BlockLocation.toBlockLocation(targetBlock.getLocation());
        if (data.alreadyExistGenerator(location)) {
            sendWithColor(player, "&cIn this site already exist a generator. &7Use /map removegenerator");
            return;
        }

        final ClickableSignGenerator generator = new ClickableSignGenerator(location, location, location, level, baseGenerator);
        data.addGenerator(generator);
        GeneratorStorage.getStorage().setLines(targetBlock, baseGenerator, level);

        sendWithColor(player, "&aGenerator set!");
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return (String[])GeneratorStorage.getStorage().getGeneratorsName().toArray();
        }
        if (args.length != 3) {
            return none();
        }
        final BaseGenerator baseGenerator = GeneratorStorage.getStorage().getGenerator(args[1]);

        if (baseGenerator == null) {
            return none();
        }

        final String[] levels = new String[baseGenerator.maxlevel()];
        for (int i = 0; i <= baseGenerator.maxlevel(); i++) {
            levels[i] = String.valueOf(i);
        }
        return levels;
    }
}