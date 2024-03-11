package lc.eggwars.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.mapsystem.CreatorData;
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
    private final InfoSubCommand info;
    private final SaveSubCommand save;

    private final MapCreatorData data;

    public MapCreatorCommand(SlimePlugin slimePlugin, EggwarsPlugin plugin, MapCreatorData data) {
        this.editor = new EditorSubCommand(data);
        this.addGenerator = new AddGeneratorSubCommand();
        this.removeGenerator = new RemoveGeneratorSubCommand();
        this.setspawn = new SetSpawnSubCommand();
        this.removeSpawn = new RemoveSpawnSubCommand();
        this.setEgg = new SetEggSubCommand();
        this.removeEgg = new RemoveEggSubCommand();
        this.maxPersons = new SetMaxPersonsSubCommand();
        this.addshopkeeper = new AddShopkeeperSubCommand();
        this.removeshopkeeper = new RemoveShoopkeperSubCommand();
        this.info = new InfoSubCommand();
        this.save = new SaveSubCommand(slimePlugin, plugin, data);
        this.data = data;
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
        final String subcommand = args[0].toLowerCase();

        if (subcommand.equals("editor")) {
            editor.handle(player, args);
            return;
        }
        if (subcommand.equals("save")) {
            save.handle(player, args);
            return;
        }

        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            sendWithColor(sender, "&cTo use this command enable editor mode. /map editor on");
            return;
        }

        switch (subcommand) {
            case "addgenerator":
                addGenerator.handle(player, args, creatorData);
                break;
            case "removegenerator":
                removeGenerator.handle(player, args, creatorData);
                break;
            case "setspawn":
                setspawn.handle(player, args, creatorData);
                break;
            case "removespawn":
                removeSpawn.handle(player, args, creatorData);
                break;
            case "setegg":
                setEgg.handle(player, args, creatorData);
                break;
            case "removeegg":
                removeEgg.handle(player, args, creatorData);
                break;
            case "setmax":
                maxPersons.handle(player, args, creatorData);
                break;
            case "addshopspawn":
                addshopkeeper.handle(player, args, creatorData);
                break;
            case "removeshopspawn":
                removeshopkeeper.handle(player, args, creatorData);
                break;
            case "info":
                info.handle(player, args, creatorData);
                break;
            default:
                sendWithColor(sender, format());
                break;
        }
        return;
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new String[] { "editor", "addgenerator", "removegenerator", "setspawn", "removespawn", "setegg", "removeegg", "addshop", "removeshopspawn", "save", "setmax", "info"};
        }
        switch (args[0].toLowerCase()) {
            case "editor": return editor.tab(sender, args);
            case "addgenerator": return addGenerator.tab(sender, args);
            case "removegenerator": return removeGenerator.tab(sender, args);
            case "setspawn": return setspawn.tab(sender, args);
            case "setegg": return setEgg.tab(sender, args);
            case "info": return info.tab(sender, args);
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
                &6setmax &7(amount)- &fSet max players per team
                &6info &7(generators/teams) - &fGet information about map
                &r
                &6save &7- &fSave all settings in the world
            """.replace('&', ChatColor.COLOR_CHAR);
    }
}