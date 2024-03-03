package lc.eggwars.teams;

import org.bukkit.Color;
import org.bukkit.scoreboard.Team;

public final class BaseTeam {
    private final String key;
    private final String name;
    private final int identifier;
    private final Color leatherColor;
    private final Team team;

    public BaseTeam(String key, String name, int identifier, Team team, Color leatherColor) {
        this.key = key;
        this.name = name;
        this.identifier = identifier;
        this.leatherColor = leatherColor;
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

    public Color getLeatherColor() {
        return leatherColor;
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
