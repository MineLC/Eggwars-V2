package lc.eggwars.teams;

import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public final class TeamStorage {
    private static TeamStorage team;

    private final Map<String, BaseTeam> teamsPerName;
    private final BaseTeam[] teams;

    TeamStorage(Map<String, BaseTeam> teamsPerName, BaseTeam[] teams) {
        this.teamsPerName = teamsPerName;
        this.teams = teams;
    }

    public BaseTeam getTeam(final String name) {
        return teamsPerName.get(name);
    }

    public BaseTeam[] getTeams() {
        return teams;
    }

    public Set<String> getTeamsName() {
        return teamsPerName.keySet();
    }

    public String tryAddTeamPrefix(final GameTeam team, final Player player) {
        return (team != null)
            ? team.getBase().getTeam().getPrefix() + player.getName()
            : player.getName();
    }

    public static TeamStorage getStorage() {
        return team;
    }

    final static void update(TeamStorage newTeam) {
        team = newTeam;
    }
}