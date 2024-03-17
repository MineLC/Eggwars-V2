package lc.eggwars.others.levels;

import org.bukkit.entity.Player;

import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;
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
        tryLevelUp(death, victim.deaths, victim, player);

        if (killer != null) {
            killer.kills++;
            tryLevelUp(kill, killer.kills, killer, player.getKiller());
        }

        if (finalKill) {
            victim.deaths++;
            tryLevelUp(finalDeath, victim.finalKills, victim, player);

            if (killer != null) {
                killer.finalKills++;
                tryLevelUp(this.finalKill, killer.finalKills, killer, player.getKiller());
            }
        }
    }

    public void win(final Player player) {
        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        data.wins++;
        tryLevelUp(wins, data.wins, data, player);
    }

    private void tryLevelUp(final LevelStat levelStat, int stats, final PlayerData data, final Player player) {
        if (stats % levelStat.need() == 0) {
            data.coins += levelStat.addlcoins();
            player.sendMessage(buildMessage(
                levelStat,
                data.level,
                data.level += levelStat.increaseLevels()));
        }
    }

    private String buildMessage(final LevelStat stat, final int oldLevel, final int newLevel) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        builder.append(stat.prefix());

        if (oldLevel != newLevel) {
            builder.append(ChatColor.GREEN);
            builder.append("    Lv ");
            builder.append((oldLevel < newLevel) ? ChatColor.GREEN : ChatColor.RED);
            builder.append(oldLevel);
            builder.append(" -> ");
            builder.append(newLevel);
            builder.append('\n');
        }
        if (stat.addlcoins() != 0) {
            builder.append(ChatColor.GOLD);
            builder.append("    LCoins ");
            builder.append((stat.addlcoins() < 0) ? '-' : '+');
            builder.append(stat.addlcoins());
            builder.append('\n');
        }
        return builder.toString();
    }

    public static LevelStorage getStorage() {
        return storage;
    }

    static void update(LevelStorage newStorage) {
        storage = newStorage;
    }
}