package lc.eggwars.game.countdown.endgame;

import java.util.Set;

import org.bukkit.entity.Player;
import org.tinylog.Logger;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.countdown.GameCountdown;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.others.spawn.SpawnStorage;
import lc.eggwars.others.tab.TabStorage;
import lc.eggwars.teams.GameTeam;

public class EndgameCountdown extends GameCountdown  {

    private final GameInProgress game;

    public EndgameCountdown(GameInProgress game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            final Set<Player> gamePlayers = game.getPlayers();
    
            for (final Player player : gamePlayers) {
                final GameTeam team = game.getTeamPerPlayer().get(player);

                for (final Player otherPlayer : gamePlayers) {
                    otherPlayer.hidePlayer(player);
                    player.hidePlayer(otherPlayer);
                }
                if (team != null) {
                    team.remove(player);
                }
                TabStorage.getStorage().removePlayers(player, gamePlayers);

                SpawnStorage.getStorage().sendToSpawn(player);
                SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);
                GameStorage.getStorage().remove(player.getUniqueId());
                player.getActivePotionEffects().forEach((potion) -> player.removePotionEffect(potion.getType()));

                TabStorage.getStorage().sendPlayerInfo(player, SpawnStorage.getStorage().getPlayers());
            }
            game.setCountdown(null);
            GameStorage.getStorage().stop(game);
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}