package lc.eggwars.utils;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collection;

public final class Chat {

    public static final void send(final String message, final Collection<? extends Player> players) {
        final BaseComponent[] components = TextComponent.fromLegacyText(message.replace('&', ChatColor.COLOR_CHAR));

        for (final Player player : players) {
            player.spigot().sendMessage(components);
        }
    }

    public static final String color(final String message) {
        return message.replace('&', ChatColor.COLOR_CHAR);
    }
}