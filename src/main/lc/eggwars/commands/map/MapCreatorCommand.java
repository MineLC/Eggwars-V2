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
    private final SetSpawnSubCommand setspawn;
    private final RemoveSpawnSubCommand removeSpawn;
    private final SetEggSubCommand setEgg;
    private final RemoveEggSubCommand removeEgg;
    private final SetMaxPersonsSubCommand maxPersons;
    private final AddShopkeeperSubCommand addshopkeeper;
    private final RemoveShoopkeperSubCommand removeshopkeeper;
    private final SaveSubCommand save;

    public MapCreatorCommand(EggwarsPlugin plugin, MapCreatorData data) {
        this.editor = new EditorSubCommand(data);
        this.addGenerator = new AddGeneratorSubCommand(data);
        this.removeGenerator = new RemoveGeneratorSubCommand(data);
        this.setspawn = new SetSpawnSubCommand(data);
        this.removeSpawn = new RemoveSpawnSubCommand(data);
        this.setEgg = new SetEggSubCommand(data);
        this.removeEgg = new RemoveEggSubCommand(data);
        this.maxPersons = new SetMaxPersonsSubCommand(data);
        this.addshopkeeper = new AddShopkeeperSubCommand(data);
        this.removeshopkeeper = new RemoveShoopkeperSubCommand(data);
        this.save = new SaveSubCommand(plugin, data);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You need be a player to use map creator");
            return true;
        } 

        if (args.length < 1) {
            sender.sendMessage(format());
            return true;
        }
    
        switch (args[0].toLowerCase()) {
            case "editor":
                editor.execute(player, args);
                break;
            case "addgenerator":
                addGenerator.execute(player, args);
                break;
            case "removegenerator":
                removeGenerator.execute(player, args);
                break;
            case "setspawn":
                setspawn.execute(player, args);
                break;
            case "removespawn":
                removeSpawn.execute(player, args);
                break;
            case "setegg":
                setEgg.execute(player, args);
                break;
            case "removeegg":
                removeEgg.execute(player, args);
                break;
            case "setmax":
                maxPersons.execute(player, args);
                break;
            case "addshopspawn":
                addshopkeeper.execute(player, args);
                break;
            case "removeshopspawn":
                removeshopkeeper.execute(player, args);
                break;
            case "save":
                save.execute(player, args);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("editor", "addgenerator", "removegenerator", "setspawn", "removespawn", "setegg", "removeegg", "addshopspawn", "removeshopspawn", "save");
        }
        switch (args[0].toLowerCase()) {
            case "editor": return editor.onTab(sender, args);
            case "addgenerator": return addGenerator.onTab(sender, args);
            case "removegenerator": return removeGenerator.onTab(sender, args);
            case "setspawn": return setspawn.onTab(sender, args);
            case "setegg": return setEgg.onTab(sender, args);
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
                &6setspawn &7(team) - &fSet spawn for a team
                &6removespawn &7(team) - &fRemove spawn of the team
                &r
                &6setegg &7(team) - &fSet egg for a team
                &6removeegg &7(team) - &fRemove egg of the team
                &r
                &6addshopspawn - &fAdd a shopkeeper spawn
                &6removeshopspawn &7(team) - &fRemove a shopkeeper spawn
                &r
                &6save &7- &fSave all settings in the world
            """.replace('&', ChatColor.COLOR_CHAR);
    }
}