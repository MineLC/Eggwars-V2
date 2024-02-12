package lc.eggwars.others.deaths;

import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.utils.Chat;

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
                plugin.getLogger().warning("The damage cause " + cause + " don't exist");
                continue;
            } 
            deathMessages[damageCause.ordinal()] = Chat.color(config.getString(cause));
        }
        return deathMessages;
    }

    public String get(final String key) {
        return Chat.color(config.getString(key));
    }
}
