package lc.eggwars.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.mapsystem.MapCreatorData;

import lc.lcspigot.commands.Command;

import net.md_5.bungee.api.ChatColor;
import net.swofty.swm.api.SlimePlugin;

public final class MapCreatorCommand implements Command {

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

    public MapCreatorCommand(SlimePlugin slimePlugin, EggwarsPlugin plugin, MapCreatorData data) {
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
        this.save = new SaveSubCommand(slimePlugin, plugin, data);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            send(sender, "You need be a player to use map creator");
            return;
        } 

        if (args.length < 1) {
            sender.sendMessage(format());
            return;
        }
    
        switch (args[0].toLowerCase()) {
            case "editor":
                editor.handle(player, args);
                break;
            case "addgenerator":
                addGenerator.handle(player, args);
                break;
            case "removegenerator":
                removeGenerator.handle(player, args);
                break;
            case "setspawn":
                setspawn.handle(player, args);
                break;
            case "removespawn":
                removeSpawn.handle(player, args);
                break;
            case "setegg":
                setEgg.handle(player, args);
                break;
            case "removeegg":
                removeEgg.handle(player, args);
                break;
            case "setmax":
                maxPersons.handle(player, args);
                break;
            case "addshopspawn":
                addshopkeeper.handle(player, args);
                break;
            case "removeshopspawn":
                removeshopkeeper.handle(player, args);
                break;
            case "save":
                save.handle(player, args);
                break;
            default:
                break;
        }
        return;
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new String[] { "editor", "addgenerator", "removegenerator", "setspawn", "removespawn", "setegg", "removeegg", "addshop", "removeshopspawn", "save", "setmax"};
        }
        switch (args[0].toLowerCase()) {
            case "editor": return editor.tab(sender, args);
            case "addgenerator": return addGenerator.tab(sender, args);
            case "removegenerator": return removeGenerator.tab(sender, args);
            case "setspawn": return setspawn.tab(sender, args);
            case "setegg": return setEgg.tab(sender, args);
            default: return none();
        }
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
                &6addshopspawn &7- &fAdd a shopkeeper spawn
                &6removeshopspawn &7(team) - &fRemove a shopkeeper spawn
                &r
                &6setmax &7(amount)- &fSet max players in game
                &r
                &6save &7- &fSave all settings in the world
            """.replace('&', ChatColor.COLOR_CHAR);
    }
}