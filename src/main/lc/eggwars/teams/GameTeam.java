package lc.eggwars.teams;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public final class GameTeam {
    private final BaseTeam baseTeam;
    private final Set<Player> players = new HashSet<>();

    private boolean hasEgg = true;
    private int playersDeath = 0;

    public GameTeam(BaseTeam team) {
        this.baseTeam = team;
    }

    public BaseTeam getBase() {
        return baseTeam;
    }

    public void add(final Player player) {
        players.add(player);
        baseTeam.getTeam().addPlayer(player);
    }

    public void remove(final Player player) {
        players.remove(player);
        playersDeath--;
        baseTeam.getTeam().removePlayer(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void addPlayerDeath() {
        playersDeath++;
    }

    public void destroyEgg() {
        hasEgg = false;
    }

    public int getPlayerDeaths() {
        return playersDeath;
    }

    public boolean hasEgg() {
        return hasEgg;
    }

    @Override
    public int hashCode() {
        return baseTeam.getIdentifier();
    }
}