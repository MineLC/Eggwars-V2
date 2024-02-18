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

    public String[] load(final EggwarsPlugin plugin) {
        final Set<String> deathCauses = config.getConfigurationSection("deaths").getKeys(false);
        final String[] deathMessages = new String[DamageCause.values().length];

        for (final String cause : deathCauses) {
            final DamageCause damageCause = DamageCause.valueOf(cause);
            if (damageCause == null) {
                Logger.warn("The damage cause " + cause + " don't exist");
                continue;
            } 
            deathMessages[damageCause.ordinal()] = Messages.color(config.getString(cause));
        }
        return deathMessages;
    }

    public String get(final String key) {
        return Messages.color(config.getString(key));
    }
}
