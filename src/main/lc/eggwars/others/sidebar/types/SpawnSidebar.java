package lc.eggwars.others.sidebar.types;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.eggwars.others.sidebar.EggwarsSidebar;
import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public final class SpawnSidebar implements EggwarsSidebar {

    private final Sidebar sidebar;
    private final String[] lines;

    public SpawnSidebar(Sidebar sidebar, String[] lines) {
        this.sidebar = sidebar;
        this.lines = lines;
    }

    @Override
    public void send(Player player) {
        final String[] parsedLines = new String[lines.length];
        final StatsEggWars stats = Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();

        final String coins = String.valueOf(stats.getLCoins());
        final String level = String.valueOf(stats.getLevel());
        final String wins =  String.valueOf(stats.getWins());
        final String kills = String.valueOf(stats.getKills());
        final String kdr =  (stats.getDeaths() == 0) ? String.valueOf(stats.getKills()) : String.valueOf((float)(stats.getKills() / stats.getDeaths()));

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                .replace("%coin%", coins)
                .replace("%level%", level)
                .replace("%wins%", wins)
                .replace("%kills%", kills)
                .replace("%kdr%", kdr);
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