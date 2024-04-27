package lc.eggwars.others.tab;

import java.util.List;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.messages.Messages;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public final class StartTab {

    public void load(final EggwarsPlugin plugin) {
        final List<String> footer = plugin.getConfig().getStringList("tab.footer");
        final List<String> header = plugin.getConfig().getStringList("tab.header");
        TabStorage.update(new TabStorage(createTab(header, footer)));
    }

    private PacketPlayOutPlayerListHeaderFooter createTab(final List<String> header, final List<String> footer) {
        final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + toString(header) + "\"}"));
        packet.b = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + toString(footer) + "\"}");
        return packet;
    }

    private String toString(final List<String> list) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (final Object objectList : list) {
            builder.append(Messages.color(objectList.toString()));
            if (++index != list.size()) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}