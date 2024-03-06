package lc.eggwars.others.levels;

import org.bukkit.entity.Player;

import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;

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
        final PlayerData victim = PlayerDataStorage.getStorage().get(player.getUniqueId());
        final PlayerData killer = (player.getKiller() != null)
            ? PlayerDataStorage.getStorage().get(player.getKiller().getUniqueId())
            : null;

        victim.deaths++;
        tryLevelUp(death, victim.deaths, victim);

        if (killer != null) {
            killer.kills++;
            tryLevelUp(kill, killer.kills, killer);
        }

        if (finalKill) {
            victim.deaths++;
            tryLevelUp(finalDeath, victim.finalKills, victim);

            if (killer != null) {
                killer.finalKills++;
                tryLevelUp(this.finalKill, killer.finalKills, killer);
            }
        }
    }

    public void win(final Player player) {
        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        data.wins++;
        tryLevelUp(wins, data.wins, data);
    }

    private void tryLevelUp(final LevelStat levelStat, int stats, final PlayerData data) {
        if (stats % levelStat.need() == 0) {
            data.level += levelStat.increaseLevels();
        }
    }

    public static LevelStorage getStorage() {
        return storage;
    }

    static void update(LevelStorage newStorage) {
        storage = newStorage;
    }
}