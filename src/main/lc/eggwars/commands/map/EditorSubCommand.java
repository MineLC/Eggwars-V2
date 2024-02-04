package lc.eggwars.commands.map;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.commands.SubCommand;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;

final class EditorSubCommand implements SubCommand {

    private final MapCreatorData data;

    EditorSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            send(sender, format());
            return;
        }
        final Player player = (Player)sender;

        switch (args[1]) {
            case "on":
                final CreatorData creatorData = data.getData(player.getUniqueId());
                if (creatorData != null ){
                    send(player, "&cYou already have enabled the editor mode");
                    return;
                }
                data.put(player.getUniqueId(), new CreatorData());
                send(player, "&aEditor mode is now enable");
                return;
        
            case "off":
                if (data.remove(player.getUniqueId())) {
                    send(player, "&6Editor mode is now disable");
                    return;
                }
                send(player, "&cTo disable editor mode, first enable it");
                return;
            default:
                send(player, format());
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return (args.length == 2) ? List.of("on", "off") : List.of();
    }

    private String format() {
        return "The format is: /map editor (on-off)";
    }
}