package lc.eggwars.teams;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lc.eggwars.EggwarsPlugin;
import net.md_5.bungee.api.ChatColor;

public class StartTeams {
    private final EggwarsPlugin plugin;

    public StartTeams(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final FileConfiguration config = plugin.loadConfig("teams");
        final Set<String> teams = config.getKeys(false);
        final BaseTeam[] baseTeams = new BaseTeam[teams.size()];
        final Map<String, BaseTeam> teamsPerName = new HashMap<>();
        int teamIndex = -1;

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (final String teamName : teams) {
            final String path = teamName + '.';
            Team team = scoreboard.getTeam((teamIndex + 1) + teamName);

            if (team == null) {
                // Example: "red" = "1red". This is used for order tablist on teams
                String newTeamName = (teamIndex + 1) + teamName;
                team = scoreboard.registerNewTeam(newTeamName);
            }

            team.setAllowFriendlyFire(false);
            team.setCanSeeFriendlyInvisibles(false);
            team.setNameTagVisibility(NameTagVisibility.ALWAYS);

            String prefix = config.getString(path + "prefix");
            String name = config.getString(path + "name");

            if (prefix != null && !prefix.isEmpty()) {
                team.setPrefix(prefix.replace('&', ChatColor.COLOR_CHAR));
            }
            if (name == null) {
                name = teamName;
            }

            final BaseTeam baseTeam = new BaseTeam(
                teamName,
                name.replace('&', ChatColor.COLOR_CHAR),
                teamIndex,
                team
            );
            baseTeams[++teamIndex] = baseTeam;
            teamsPerName.put(teamName, baseTeam);
        }
        TeamStorage.update(new TeamStorage(teamsPerName, baseTeams));
    }
}