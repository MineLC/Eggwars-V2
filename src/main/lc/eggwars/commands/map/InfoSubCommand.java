package lc.eggwars.commands.map;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.eggwars.game.clickable.ClickableSignGenerator;
import lc.eggwars.mapsystem.CreatorData;
import lc.eggwars.mapsystem.MapCreatorData;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.TeamStorage;
import lc.eggwars.utils.BlockLocation;
import lc.lcspigot.commands.Command;

final class InfoSubCommand implements Command {

    private final MapCreatorData data;

    InfoSubCommand(MapCreatorData data) {
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            sendWithColor(player, "&cTo use this command enable the editor mode");
            return;
        }

        if (args.length < 2) {
            sendWithColor(player, "&cFormat: /map info (generators/teams)");
            return;
        }
 
        switch (args[1].toLowerCase()) {
            case "teams":
                sendWithColor(sender, buildTeamInfo(creatorData));
                break;
            case "generators":
                sendWithColor(sender, buildGeneratorsInfo(creatorData));
                break;
            default:
                break;
        }
    }

    private final String buildTeamInfo(final CreatorData data) {
        final StringBuilder builder = new StringBuilder();
        final BaseTeam[] teams = TeamStorage.getStorage().getTeams();
        int amountTeams = 0;

        for (final BaseTeam team : teams) {
            final BlockLocation spawn = data.getSpawnsMap().get(team);
            final BlockLocation egg = data.getEggsMap().get(team);

            if (spawn == null && egg == null) {
                continue;
            }
            builder.append(team.getName());
            builder.append(" &7- SPAWN: &e");
            builder.append(spawn);
            builder.append(" &7- EGG: &e");
            builder.append(egg);
            builder.append('\n');
            ++amountTeams;
        }
        if (amountTeams != 0) {
            builder.append("&bAmount Teams: ");
            builder.append(amountTeams);
            builder.append('\n');
            builder.append("&bMax players: ");
            builder.append(amountTeams * data.getMaxPersonsPerTeam());
            return builder.toString();
        }
        return "&cNo teams found";
    }


    private final String buildGeneratorsInfo(final CreatorData data) {
        final Collection<ClickableSignGenerator> generators = data.getGeneratorsMap().values();
        if (generators == null || generators.isEmpty()) {
            return "&cNo generators found";
        }

        final StringBuilder builder = new StringBuilder();

        for (final ClickableSignGenerator generator : generators) {
            builder.append(generator.getBase().name());
            builder.append(" &7- LOC: &e");
            builder.append(generator.getLocation());
            builder.append('\n');
        }

        return builder.toString();
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 1) ? list("generators", "teams") : none();
    }
}