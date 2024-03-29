package lc.eggwars.teams;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public final class GameTeam {
    private final BaseTeam baseTeam;
    private final Set<Player> players = new HashSet<>();

    private boolean hasEgg = true;
    private int playersWithLive = 0;

    public GameTeam(BaseTeam team) {
        this.baseTeam = team;
    }

    public BaseTeam getBase() {
        return baseTeam;
    }

    public void add(final Player player) {
        players.add(player);
        playersWithLive++;
        baseTeam.getTeam().addPlayer(player);
    }

    public void remove(final Player player) {
        players.remove(player);
        baseTeam.getTeam().removePlayer(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void removeOnePlayerWithLive() {
        playersWithLive--;
    }

    public void destroyEgg() {
        hasEgg = false;
    }

    public int getPlayersWithLive() {
        return playersWithLive;
    }

    public boolean hasEgg() {
        return hasEgg;
    }

    @Override
    public int hashCode() {
        return baseTeam.getIdentifier();
    }
}