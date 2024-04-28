package lc.eggwars.others.levels;

import org.bukkit.entity.Player;

import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import net.md_5.bungee.api.ChatColor;

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
        tryGainRewards(death, victim.deaths, victim, player);

        if (killer != null) {
            killer.kills++;
            tryGainRewards(kill, killer.kills, killer, player.getKiller());
        }

        if (finalKill) {
            victim.deaths++;
            tryGainRewards(finalDeath, victim.finalKills, victim, player);

            if (killer != null) {
                killer.finalKills++;
                tryGainRewards(this.finalKill, killer.finalKills, killer, player.getKiller());
            }
        }
    }

    public void win(final Player player) {
        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        data.wins++;
        tryGainRewards(wins, data.wins, data, player);
    }

    private void tryGainRewards(final LevelStat levelStat, int stats, final PlayerData data, final Player player) {
        if (levelStat.lcoinsEvery() > 0 && stats % levelStat.lcoinsEvery() == 0) {
            data.coins += levelStat.addlcoins();
            player.sendMessage(buildLcoinsMessage(levelStat));
        }

        if (levelStat.levelUpEvery() > 0 && stats % levelStat.levelUpEvery() == 0) {
            player.sendMessage(buildLevelUpMessage(levelStat, data.level, data.level++));
        }
    }

    public int getLevels(final PlayerData data) {
        int levels = 0;
        levels += (kill.levelUpEvery() > 0) ? data.kills % kill.levelUpEvery() : 0;
        levels += (death.levelUpEvery() > 0) ? data.deaths % death.levelUpEvery() : 0;
        levels += (wins.levelUpEvery() > 0) ? data.wins % wins.levelUpEvery() : 0;
        levels += (finalKill.levelUpEvery() > 0) ? data.finalKills % finalKill.levelUpEvery() : 0;
        levels += (finalDeath.levelUpEvery() > 0) ? data.finalDeaths % finalDeath.levelUpEvery() : 0;
        return levels;
    }

    private String buildLcoinsMessage(final LevelStat stat) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        builder.append(stat.prefix());
        if (stat.addlcoins() != 0) {
            builder.append(ChatColor.GOLD);
            builder.append("    LCoins ");
            builder.append((stat.addlcoins() < 0) ? '-' : '+');
            builder.append(stat.addlcoins());
            builder.append('\n');
        }
        return builder.toString();
    }

    private String buildLevelUpMessage(final LevelStat stat, final int oldLevel, final int newLevel) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        builder.append(stat.prefix());
        builder.append(ChatColor.GREEN);
        builder.append("    Nivel ");
        builder.append(oldLevel);
        builder.append(" -> ");
        builder.append(newLevel);
        builder.append('\n');

        return builder.toString();
    }

    public static LevelStorage getStorage() {
        return storage;
    }

    static void update(LevelStorage newStorage) {
        storage = newStorage;
    }
}