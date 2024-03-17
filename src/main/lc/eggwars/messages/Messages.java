package lc.eggwars.messages;

import java.util.Collection;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class Messages {
    private static Messages instance;
    private final Map<String, String> parsedMessages;

    Messages(Map<String, String> parsedMessages) {
        this.parsedMessages = parsedMessages;
    }

    public static final void send(final CommandSender sender, final String key) {
        final String message = instance.parsedMessages.get(key);
        if (message != null) {
            sender.sendMessage(message);
        }
    }

    public static final void send(final Collection<Player> senders, final String key) {
        final String message = instance.parsedMessages.get(key);
        if (message != null) {
            final BaseComponent[] components = TextComponent.fromLegacyText(message);
            for (Player sender : senders) {
                sender.spigot().sendMessage(components);
            }
        }
    }

    public static final void sendNoGet(final Collection<Player> senders, final String message) {
        final BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
        for(final Player sender : senders) {
            sender.spigot().sendMessage(components);
        }
    }

    public static final String get(final String key) {
        final String message = instance.parsedMessages.get(key);
        return (message == null) ? "Can't found the message " + key : message;
    }

    public static final String color(final String message) {
        return (message == null) ? "" : message.replace('&', ChatColor.COLOR_CHAR);
    }

    static final void update(Messages messages) {
        instance = messages;
    }
}