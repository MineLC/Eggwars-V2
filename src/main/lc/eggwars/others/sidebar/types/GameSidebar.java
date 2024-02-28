package lc.eggwars.others.sidebar.types;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.sidebar.EggwarsSidebar;
import lc.eggwars.teams.BaseTeam;
import net.md_5.bungee.api.ChatColor;

public final class GameSidebar implements EggwarsSidebar {
    private final Sidebar sidebar;
    private final String[] footer;

    public GameSidebar(Sidebar sidebar, String[] footer) {
        this.sidebar = sidebar;
        this.footer = footer;
    }

    @Override
    public void send(Player player) {
        sidebar.setLines(createGameLines(player));
        sidebar.sendLines(player);
        sidebar.sendTitle(player);
    }

    @Override
    public void send(Collection<Player> players) {
        sidebar.setLines(createGameLines(players.iterator().next()));

        for (final Player player : players) {
            sidebar.sendLines(player);
            sidebar.sendTitle(player);
        }
    }

    private final Object[] createGameLines(final Player player) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            return sidebar.createLines(footer);
        }

        final Set<Entry<BaseTeam, Set<Player>>> playersPerTeam = game.getPlayersInTeam().entrySet();
        final Set<Player> playersLiving = game.getPlayersLiving();

        final int amountTeams = game.getMapData().getSpawns().size();
        final String[] teamLines = new String[amountTeams + footer.length];
        int score = 0;

        for (final Entry<BaseTeam, Set<Player>> team : playersPerTeam) {
            final Set<Player> players = team.getValue();
            int amountLive = 0;
            for (final Player teamPlayer : players) {
                if (playersLiving.contains(teamPlayer)) {
                    amountLive++;
                }
            }
            final BaseTeam baseTeam = team.getKey();

            if (amountLive == 0) {
                teamLines[score++] = createTeamLine(baseTeam.getName(), 'c', 'X', false);
                continue;
            }

            if (game.getTeamsWithEgg().contains(baseTeam)) {
                teamLines[score++] = createTeamLine(baseTeam.getName(), 'a', '✓', false);
                continue;
            }
            teamLines[score++] = createTeamLine(baseTeam.getName(), '7', (char)amountLive, true);
        }

        for (int i = 0; i < footer.length; i++) {
            teamLines[score + i] = footer[i];
        }
        return sidebar.createLines(teamLines);
    }

    private String createTeamLine(final String teamName, final char color, final char value, final boolean number) {
        final StringBuilder builder = new StringBuilder(teamName.length() + 5);
        builder.append(teamName);
        builder.append(' ');

        builder.append(ChatColor.COLOR_CHAR);
        builder.append((number) ? (int)value : value);

        return builder.toString();
    }
}