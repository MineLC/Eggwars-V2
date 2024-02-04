package lc.eggwars.commands.map;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.mapsystem.MapCreatorData;
import net.md_5.bungee.api.ChatColor;

public final class MapCreatorCommand implements TabExecutor {

    private final EditorSubCommand editor;
    private final AddGeneratorSubCommand addGenerator;
    private final RemoveGeneratorSubCommand removeGenerator;
    private final AddSpawnSubCommand addSpawn;
    private final RemoveSpawnSubCommand removeSpawn;
    private final SaveSubCommand save;

    public MapCreatorCommand(EggwarsPlugin plugin, MapCreatorData data) {
        this.editor = new EditorSubCommand(data);
        this.addGenerator = new AddGeneratorSubCommand(data);
        this.removeGenerator = new RemoveGeneratorSubCommand(data);
        this.addSpawn = new AddSpawnSubCommand(data);
        this.removeSpawn = new RemoveSpawnSubCommand(data);
        this.save = new SaveSubCommand(plugin, data);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You need be a player to use map creator");
            return true;
        } 

        if (args.length < 1) {
            sender.sendMessage(format());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "editor":
                editor.execute(sender, args);
                break;
            case "addgenerator":
                addGenerator.execute(sender, args);
                break;
            case "removegenerator":
                removeGenerator.execute(sender, args);
                break;
            case "addspawn":
                addSpawn.execute(sender, args);
                break;
            case "removespawn":
                removeSpawn.execute(sender, args);
                break;
            case "save":
                save.execute(sender, args);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("editor", "addgenerator", "removegenerator", "addspawn", "removespawn", "save");
        }
        switch (args[0].toLowerCase()) {
            case "editor": return editor.onTab(sender, args);
            case "addgenerator": return addGenerator.onTab(sender, args);
            case "removegenerator": return removeGenerator.onTab(sender, args);
            case "addspawn": return addSpawn.onTab(sender, args);
        }
        return List.of();
    }

    private String format() {
        return """
              &6&lMap creator &7(MineLC)
              &r
              &e/map &7->
                &6editor &7(on-off) - &fActivate/Disable the editor mode
                &r
                &6addgenerator &7(generatorName) (level) - &fSet a generator
                &6removegenerator &7- &fRemove last generator
                &r
                &6addspawn &7(team) - &fAdd spawn for a team
                &6removespawn &7(team) - &fRemove last spawn of a team
                &r
                &6save &7- &fSave all settings in the world
            """.replace('&', ChatColor.COLOR_CHAR);
    }
}