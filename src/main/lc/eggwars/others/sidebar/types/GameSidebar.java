package lc.eggwars.others.sidebar.types;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.sidebar.EggwarsSidebar;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;

public final class GameSidebar implements EggwarsSidebar {
    private final String title;

    public GameSidebar(String title) {
        this.title = title;
    }

    @Override
    public void send(Player player) {
        send(List.of(player));
    }

    @Override
    public void send(Collection<Player> players) {
        final GameInProgress game = GameStorage.getStorage().getGame(players.iterator().next().getUniqueId());
        if (game == null) {
            return;
        }
        final Sidebar sidebar = new LightSidebarLib().createSidebar();

        sidebar.setLines(createGameLines(sidebar, game));
        sidebar.setTitle(title);

        for (final Player player : players) {
            sidebar.sendLines(player);
            sidebar.sendTitle(player);
        }
    }

    private final PacketPlayOutScoreboardScore[] createGameLines(final Sidebar sidebar, final GameInProgress game) {
        final Set<GameTeam> teams = game.getTeams();

        final int amountTeams = teams.size();
        final String[] teamLines = new String[amountTeams];
        final int[] scores = new int[amountTeams];
        int score = -1;

        for (final GameTeam team : teams) {
            final int amountLive = team.getPlayersWithLive();
            final BaseTeam baseTeam = team.getBase();

            scores[++score] = amountLive;
            teamLines[score] = team.hasEgg()
                ? ChatColor.GREEN.toString() + "✔ " + baseTeam.getName()
                : ChatColor.RED.toString() + "✘ " + baseTeam.getName();
        }
        final PacketPlayOutScoreboardScore[] lines = (PacketPlayOutScoreboardScore[]) sidebar.createLines(teamLines);
        for (int i = 0; i < amountTeams; i++) {
            lines[i].c = scores[i];
        }
        return lines;
    }
}