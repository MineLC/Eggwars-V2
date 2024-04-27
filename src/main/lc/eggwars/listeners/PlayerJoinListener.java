package lc.eggwars.listeners;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import obed.me.lccommons.api.entities.PlayerData;
import obed.me.lccommons.api.services.UserProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.database.mongodb.MongoDBManager;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.others.tab.TabStorage;

public final class PlayerJoinListener implements EventListener {

    private final String joinMessage;

    public PlayerJoinListener(String joinMessage) {
        this.joinMessage = joinMessage;
    }

    @ListenerData(
        event = PlayerJoinEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerJoinEvent event = (PlayerJoinEvent)defaultEvent;
        final Player player = event.getPlayer();
        event.setJoinMessage(null);

        SpawnStorage.getStorage().sendToSpawn(player);
        TabStorage.getStorage().sendTab(player);

        final List<Player> players = SpawnStorage.getStorage().getPlayers();

        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }

        CompletableFuture.runAsync(() -> {
            final lc.eggwars.database.mongodb.PlayerData data = MongoDBManager.getManager().getData(player.getUniqueId());
            PlayerData playerData = UserProvider.getInstance().getUserByName(player.getName());
            data.coins = playerData.getCoins();
            PlayerDataStorage.getStorage().add(player.getUniqueId(), data);
            SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);            

            final PlayerData pp = UserProvider.getInstance().getUserCache(player.getName());
            final String playerInfo = pp.getRankInfo().getRank().getPrefix() + " &7" + pp.getRankInfo().getUserColor() + player.getName();
            Messages.sendNoGet(SpawnStorage.getStorage().getPlayers(), playerInfo + joinMessage);
            TabStorage.getStorage().sendPlayerInfo(player, players);
        });
    }
}