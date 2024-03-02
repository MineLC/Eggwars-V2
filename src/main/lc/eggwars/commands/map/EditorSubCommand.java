package lc.eggwars.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;

final class EditorSubCommand implements Command {

    private final MapCreatorData data;

    EditorSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        if (args.length != 2) {
            send(player, format());
            return;
        }

        switch (args[1]) {
            case "on":
                final CreatorData creatorData = data.getData(player.getUniqueId());
                if (creatorData != null ){
                    sendWithColor(player, "&cYou already have enabled the editor mode");
                    return;
                }
                data.put(player.getUniqueId(), new CreatorData());
                sendWithColor(player, "&aEditor mode is now enable");
                return;
        
            case "off":
                if (data.remove(player.getUniqueId())) {
                    sendWithColor(player, "&6Editor mode is now disable");
                    return;
                }
                sendWithColor(player, "&cTo disable editor mode, first enable it");
                return;
            default:
                send(player, format());
        }
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 2) ? new String[] {"on", "off"} : none();
    }

    private String format() {
        return "The format is: /map editor (on-off)";
    }
}