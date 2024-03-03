package lc.eggwars.others.levels;

import org.bukkit.entity.Player;

import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public class LevelManager {

    // TODO - Make configurable
    private static final int LEVELS_ON_FINAL_KILL = 5;
    private static final int LEVELS_ON_FINAL_DEATH = -5;

    private static final int LEVELS_ON_DEATH = -1;
    private static final int LEVELS_ON_KILL = -5;

    public void onDeath(final Player player, final boolean finalKill) {
        final StatsEggWars victim = get(player);
        final StatsEggWars killer = (player.getKiller() != null) ? get(player) : null;

        victim.setDeaths(victim.getDeaths() + 1);
        victim.setLevel(victim.getLevel() + LEVELS_ON_DEATH);

        if (killer != null) {
            killer.setKills(victim.getKills() + 1);
            killer.setLevel(killer.getLevel() + LEVELS_ON_KILL);
        }

        if (finalKill) {
            victim.setLastDeath(victim.getDeaths() + 1);
            victim.setLevel(victim.getLevel() + LEVELS_ON_FINAL_DEATH);
    
            if (killer != null) {
                killer.setLastKill(victim.getKills() + 1);  
                killer.setLevel(killer.getLevel() + LEVELS_ON_FINAL_KILL);
            }
        }
    }

    private StatsEggWars get(final Player player) {
        return Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();
    }
}