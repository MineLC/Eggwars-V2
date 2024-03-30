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

public final class PregameSidebar implements EggwarsSidebar {

    private final String[] lines;
    private final String title;

    public PregameSidebar(String[] lines, String title) {
        this.lines = lines;
        this.title = title;
    }

    @Override
    public void send(Player player) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null || game.getState() != GameState.PREGAME) {
            return;
        }

        final PlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());

        final String amountPlayers = String.valueOf(game.getPlayers().size());
        final String lcoins = String.valueOf(data.coins);
        final String maxPlayers = String.valueOf(game.getMapData().getMaxPlayers());
        
        final String[] parsedLines = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                .replace("%coin%", lcoins)
                .replace("%players%", amountPlayers)
                .replace("%max%", maxPlayers);
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