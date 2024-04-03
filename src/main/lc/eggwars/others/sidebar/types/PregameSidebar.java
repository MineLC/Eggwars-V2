package lc.eggwars.others.sidebar.types;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.eggwars.database.mongodb.PlayerData;
import lc.eggwars.database.mongodb.PlayerDataStorage;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.sidebar.EggwarsSidebar;
import net.md_5.bungee.api.ChatColor;

public final class PregameSidebar implements EggwarsSidebar {

    private final String[] lines;
    private final String title, team, solo;

    public PregameSidebar(String[] lines, String title, String team, String solo) {
        this.lines = lines;
        this.title = title;
        this.team = team;
        this.solo = solo;
    }

    @Override
    public void send(Player player) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null || game.getState() != GameState.PREGAME) {
            return;
        }

        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());

        final String lcoins = String.valueOf(data.coins);
        final String players = game.getPlayers().size() + "/" + ChatColor.GOLD + game.getMapData().getMaxPlayers();
        final String mode = (game.getMapData().getMaxPersonsPerTeam() >= 1) ? team : solo;
        final String[] parsedLines = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                .replace("%coin%", lcoins)
                .replace("%players%", players)
                .replace("%mode%", mode)
                .replace("%map%", game.getMapData().toString());
        }
        final Sidebar sidebar = new LightSidebarLib().createSidebar();
        final Object[] lines = sidebar.createLines(parsedLines);

        sidebar.setTitle(title);
        sidebar.setLines(lines);
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