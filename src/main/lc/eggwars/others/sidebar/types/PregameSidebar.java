package lc.eggwars.others.sidebar.types;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.Sidebar;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.countdown.types.PreGameCountdown;
import lc.eggwars.others.sidebar.EggwarsSidebar;

import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public final class PregameSidebar implements EggwarsSidebar {

    private final Sidebar sidebar;
    private final String[] lines;

    public PregameSidebar(Sidebar sidebar, String[] lines) {
        this.sidebar = sidebar;
        this.lines = lines;
    }

    @Override
    public void send(Player player) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null || !(game.getCountdown() instanceof PreGameCountdown pregame)) {
            return;
        }
        final StatsEggWars stats = Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();
        
        final String amountPlayers = String.valueOf(game.getPlayers().size());
        final String lcoins = String.valueOf(stats.getLCoins());
        final String maxPlayers = String.valueOf(game.getMapData().getMaxPlayers());
        final String countdown = pregame.getCountdown();
        
        final String[] parsedLines = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                .replace("%timer%", countdown)
                .replace("%coin%", lcoins)
                .replace("%players%", amountPlayers)
                .replace("%max%", maxPlayers);
        }
        sidebar.setLines(sidebar.createLines(parsedLines));
        sidebar.sendLines(player);
        sidebar.sendTitle(player);
    }

    @Override
    public void send(Collection<Player> players) {
        for (final Player player : players) {
            send(player);
        }
    }
}