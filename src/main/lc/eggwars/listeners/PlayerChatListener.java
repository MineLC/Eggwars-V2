package lc.eggwars.listeners;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.teams.GameTeam;
import lc.eggwars.teams.TeamStorage;
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
        if (SpawnStorage.getStorage().isInSpawn(p)) {
            final String global_format = pp.getRankInfo().getRank().getPrefix() + " &7" + pp.getRankInfo().getUserColor() + p.getName() + " &8» &f" + message;
            Messages.sendNoGet(SpawnStorage.getStorage().getPlayers(),  global_format);
            return;
        }
        final GameInProgress game = GameStorage.getStorage().getGame(p.getUniqueId());

        if (game == null) {
            return;
        }
        if (game.getState() == GameState.PREGAME) {
            final String global_format = pp.getRankInfo().getRank().getPrefix() + " &7" + pp.getRankInfo().getUserColor() + TeamStorage.getStorage().tryAddTeamPrefix(game.getTeamPerPlayer().get(p), p) + " &8» &f" + message;
            Messages.sendNoGet(game.getPlayers(),  global_format);
            return;
        }

        final GameTeam team = game.getTeamPerPlayer().get(p);
        if (team == null) {
            return;
        }

        if(p.getGameMode() == GameMode.SPECTATOR){
            final String spectatorMessage = "&8&lEspectador " + TeamStorage.getStorage().tryAddTeamPrefix(team, p) + " &8» &f" + message;
            Messages.sendNoGet(game.getPlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList()), spectatorMessage);

            return;
        }

        if(event.getMessage().charAt(0) == '!'){
            final String globalMessage = "&6&lGLOBAL " + TeamStorage.getStorage().tryAddTeamPrefix(team, p) + " &8» &f" + message.substring(1);
            Messages.sendNoGet(game.getPlayers(), globalMessage);
            return;
        }

        final String teamMessage = "&b&lEQUIPO " + TeamStorage.getStorage().tryAddTeamPrefix(team, p) + " &8» &f" + message;
        Messages.sendNoGet(team.getPlayers(), teamMessage);
    }
}
