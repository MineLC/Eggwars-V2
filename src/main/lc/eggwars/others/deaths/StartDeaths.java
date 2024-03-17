package lc.eggwars.others.deaths;

import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.tinylog.Logger;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.messages.Messages;

public final class StartDeaths {

    private final FileConfiguration config;

    public StartDeaths(EggwarsPlugin plugin) {
        this.config = plugin.loadConfig("deaths");
    }

    public void load(final EggwarsPlugin plugin) {
        final String[] deathMessages = getDeathMessages();

        DeathStorage.update(new DeathStorage(
            plugin,
            deathMessages,
            get("fallback-message"),
            get("final-death-prefix"),
            get("suffix-if-killer-exist"),
            get("respawn.title"),
            get("subtitle.title"),
            config.getInt("respawn.seconds")));
    }

    private String[] getDeathMessages() {
        final Set<String> deathCauses = config.getConfigurationSection("deaths").getKeys(false);
        final String[] deathMessages = new String[DamageCause.values().length];

        for (final String cause : deathCauses) {
            final DamageCause damageCause = DamageCause.valueOf(cause);
            if (damageCause == null) {
                Logger.warn("The damage cause " + cause + " don't exist");
                continue;
            } 
            deathMessages[damageCause.ordinal()] = Messages.color(config.getString("deaths." + cause));
        }
        return deathMessages;
    }

    private String get(final String key) {
        return Messages.color(config.getString(key));
    }
}
