package lc.eggwars.others.sidebar.types;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.eggwars.database.PlayerData;
import lc.eggwars.database.PlayerDataStorage;
import lc.eggwars.others.sidebar.EggwarsSidebar;

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
        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());

        final String coins = String.valueOf(data.coins);
        final String level = String.valueOf(data.level);
        final String wins =  String.valueOf(data.wins);
        final String kills = String.valueOf(data.kills);
        final String kdr =  (data.deaths == 0) ? String.valueOf(data.kills) : String.valueOf((float)(data.kills / data.deaths));

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