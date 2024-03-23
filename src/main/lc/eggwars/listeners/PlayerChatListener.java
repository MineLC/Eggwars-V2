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
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
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

        if(!p.hasPermission("minelc.vip")) {
            message = StringUtils.remove(message , '&');
        }

        GameInProgress game = GameStorage.getStorage().getGame(p.getUniqueId());

        if (game == null) {
            return;
        }

        if(game.getState() == GameState.PREGAME){
            final String global_format = String.format(
                "%s %s &8» &7%s",
                pp.getRankInfo().getRank().getPrefix(), "&7" + pp.getRankInfo().getUserColor() + p.getName(), message);

            final Collection<Player> players = (game.getState() == GameState.PREGAME)
                ? game.getPlayers()
                : SpawnStorage.getStorage().location().getWorld().getPlayers();

                Messages.sendNoGet(players,  global_format);

            return;
        }
    
        if(p.getGameMode() == GameMode.SPECTATOR){
            final String spectatorMessage = String.format("%s &7%s &8» &7%s", "&8&lEspectador", p.getName(), message);

            Messages.sendNoGet(game.getPlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList()), spectatorMessage);

            return;
        }
        GameTeam team = game.getTeams().stream().filter(t -> t.getPlayers().contains(p)).findFirst().orElse(null);
        if(team == null) return;

        if(event.getMessage().charAt(0) == '!'){
            final String globalMessage = String.format("&6&lGLOBAL %s%s &8» &7%s",  team.getBase().getTeam().getPrefix(), p.getName(), message);
            Messages.sendNoGet(game.getPlayers(), globalMessage.substring(0));
            return;
        }

        final String teamMessage = String.format("&b&lEQUIPO %s%s &8» &7%s", team.getBase().getTeam().getPrefix(), p.getName(), message);
        Messages.sendNoGet(team.getPlayers(), teamMessage);
    }
}
