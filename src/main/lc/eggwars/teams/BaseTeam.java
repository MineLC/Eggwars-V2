package lc.eggwars.teams;

import org.bukkit.scoreboard.Team;

public final class BaseTeam {
    private final String key;
    private final String name;
    private final int identifier;
    private final Team team;

    public BaseTeam(String key, String name, int identifier, Team team) {
        this.key = key;
        this.name = name;
        this.identifier = identifier;
        this.team = team;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getIdentifier() {
        return identifier;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public final boolean equals(Object object) {
        return (object instanceof BaseTeam otherTeam) ? otherTeam.identifier == this.identifier : false;
    }

    @Override
    public final int hashCode() {
        return identifier;
    }
}
