package lc.eggwars.messages;

import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;

import java.util.HashMap;
import java.util.List;

public class StartMessages {

    public void load(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("messages");

        final Set<String> messages = config.getKeys(false);
        final Map<String, String> parsedMessages = new HashMap<>(messages.size());

        for (final String key : messages) {
            final Set<String> subMessages = config.getConfigurationSection(key).getKeys(false);

            for (final String message : subMessages) {
                final String path = key + "." + message;
                final Object object = config.get(path);
                if (object instanceof String) {
                    parsedMessages.put(path, Messages.color(object.toString()));
                    continue;
                }
                if (!(object instanceof List<?> list)) {
                    parsedMessages.put(path, object.toString());
                    continue;
                }
                final StringBuilder builder = new StringBuilder();
                for (final Object objectList : list) {
                    builder.append(Messages.color(objectList.toString()));
                }
                parsedMessages.put(path, builder.toString());
            }
        }
        Messages.update(new Messages(parsedMessages));
    }
}