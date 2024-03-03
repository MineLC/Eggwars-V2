package lc.eggwars.others.levels;

import org.bukkit.entity.Player;

import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public class LevelStorage {

    private static LevelStorage storage;
    private final LevelStat kill, death, finalKill, finalDeath, wins;

    LevelStorage(LevelStat kill, LevelStat death, LevelStat finalKill, LevelStat finalDeath, LevelStat wins) {
        this.kill = kill;
        this.death = death;
        this.finalKill = finalKill;
        this.finalDeath = finalDeath;
        this.wins = wins;
    }

    public void onDeath(final Player player, final boolean finalKill) {
        final StatsEggWars victim = get(player);
        final StatsEggWars killer = (player.getKiller() != null) ? get(player) : null;

        victim.setDeaths(victim.getDeaths() + 1);
        tryLevelUp(death, victim.getDeaths(), victim);

        if (killer != null) {
            killer.setKills(victim.getKills() + 1);
            tryLevelUp(kill, killer.getKills(), killer);
        }

        if (finalKill) {
            victim.setLastDeath(victim.getDeaths() + 1);
            tryLevelUp(finalDeath, victim.getLastDeath(), victim);

            if (killer != null) {
                killer.setLastKill(victim.getKills() + 1);  
                tryLevelUp(this.finalKill, killer.getLastKill(), killer);
            }
        }
    }

    public void win(final Player player) {
        final StatsEggWars stats = get(player);
        stats.setWins(stats.getWins() + 1);
        tryLevelUp(wins, stats.getWins(), stats);
    }

    private void tryLevelUp(final LevelStat levelStat, int stats, final StatsEggWars statsEggWars) {
        if (stats % levelStat.need() == 0) {
            statsEggWars.setLevel(statsEggWars.getLevel() + levelStat.increaseLevels());
        }
    }

    private StatsEggWars get(final Player player) {
        return Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();
    }

    public static LevelStorage getStorage() {
        return storage;
    }

    static void update(LevelStorage newStorage) {
        storage = newStorage;
    }
}