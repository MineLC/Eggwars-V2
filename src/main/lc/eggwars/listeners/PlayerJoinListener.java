package lc.eggwars.listeners;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import lc.eggwars.database.mongodb.MongoDBManager;
import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;

public final class PlayerJoinListener implements EventListener {

    private final PacketPlayOutPlayerListHeaderFooter packetTab;

    public PlayerJoinListener(final List<String> header, final List<String> footer) {
        this.packetTab = createTab(header, footer);
    }

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        final Player player = event.getPlayer();
        event.setJoinMessage(null);
        SpawnStorage.getStorage().sendToSpawn(event.getPlayer());
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }
        ((CraftPlayer)player).getHandle().playerConnection.networkManager.handle(packetTab);

        CompletableFuture.runAsync(() -> {
            final PlayerData data = MongoDBManager.getManager().getData(player.getUniqueId());
            PlayerDataStorage.getStorage().add(player.getUniqueId(), data);
            SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);            
        });       
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