package lc.eggwars.listeners;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.teams.GameTeam;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import obed.me.lccommons.api.entities.PlayerData;
import obed.me.lccommons.api.services.UserProvider;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.stream.Collectors;


public class PlayerChatListener implements EventListener {

    @ListenerData(
            priority = EventPriority.LOWEST,
            event = AsyncPlayerChatEvent.class
    )
    public void handle(Event e){
        final AsyncPlayerChatEvent event = (AsyncPlayerChatEvent)e;
        event.setCancelled(true);
        Player p = event.getPlayer();
        PlayerData pp = UserProvider.getInstance().getUserCache(p.getName());
        if(pp == null) return;

        String message = event.getMessage();
        if(!p.hasPermission("minelc.vip"))
            message = ChatColor.stripColor(message);

        GameInProgress game = GameStorage.getStorage().getGame(p.getUniqueId());
        String global_format = String.format("%s %s &8» &7%s",
                pp.getRankInfo().getRank().getPrefix(), "&7" + p.getName(), message);

        if(game == null){
            Messages.sendNoGet(SpawnStorage.getStorage().location().getWorld().getPlayers(), global_format);
            return;
        }
        if(game.getState() == GameState.PREGAME){
            Messages.sendNoGet(game.getWorld().getPlayers(), global_format);
            return;
        }
        String formatted = String.format("%s %s &8» &7%s", "&8(Espectador)", p.getName(), message);

        if(p.getGameMode() == GameMode.SPECTATOR){
            Messages.sendNoGet(game.getPlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                    .collect(Collectors.toList()), formatted);

            return;
        }
        GameTeam team = game.getTeams().stream().filter(t -> t.getPlayers().contains(p)).findFirst().orElse(null);
        if(team == null) return;
        formatted = String.format("&6(Global) %s %s &8» &7%s", team.getBase().getKey(), p.getName(), message);

        if(event.getMessage().startsWith("!")){
            Messages.sendNoGet(game.getPlayers(), formatted);
            return;
        }
        formatted = String.format("%s %s &8» &7%s", team.getBase().getKey(), p.getName(), message);
        Messages.sendNoGet(team.getPlayers(), formatted);


    }
}
