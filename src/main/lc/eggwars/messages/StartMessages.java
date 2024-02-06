package lc.eggwars.messages;

import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.utils.Chat;

import java.util.HashMap;
import java.util.List;

public class StartMessages {

    public Messages load(EggwarsPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("messages");

        final Set<String> messages = config.getKeys(false);
        final Map<String, String> parsedMessages = new HashMap<>(messages.size());

        for (final String key : messages) {
            final Object object = config.get(key);
            if (object instanceof String) {
                parsedMessages.put(key, Chat.color(object.toString()));
                continue;
            }
            if (!(object instanceof List<?> list)) {
                parsedMessages.put(key, object.toString());
                continue;
            }
            final StringBuilder builder = new StringBuilder();
            for (final Object objectList : list) {
                builder.append(Chat.color(objectList.toString()));
            }
            parsedMessages.put(key, builder.toString());
        }

        return new Messages(parsedMessages);
    }
}