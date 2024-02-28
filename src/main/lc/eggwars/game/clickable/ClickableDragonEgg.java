package lc.eggwars.game.clickable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;
import obed.me.minecore.objects.Jugador;
import obed.me.minecore.objects.stats.servers.StatsEggWars;

public final class ClickableDragonEgg implements ClickableBlock  {
    private final BaseTeam team;
    private final BlockLocation location;

    public ClickableDragonEgg(BaseTeam team, BlockLocation location) {
        this.team = team;
        this.location = location;
    }

    @Override
    public void onClick(final Player player, final Action action) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null || game.getState() != GameState.IN_GAME || !game.getTeamsWithEgg().contains(team)) {
            return;
        }

        final BaseTeam playerTeam = game.getTeamPerPlayer().get(player);
        if (playerTeam == null) {
            return;
        }
        if (team.equals(playerTeam)) {
            Messages.send(player, "eggs.break-it-egg");
            return;
        }

        game.getTeamsWithEgg().remove(team);
        player.getWorld().getBlockAt(location.x(), location.y(), location.z()).setType(Material.AIR);

        final String eggBreaked = Messages.get("eggs.break-other").replace("%team%", team.getName()).replace("%player%", player.getName());
        Messages.sendNoGet(game.getPlayers(), eggBreaked);

        final StatsEggWars stats = Jugador.getJugador(player.getName()).getServerStats().getStatsEggWars();
        stats.setDestroyedEggs(stats.getDestroyedEggs() + 1);
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());
    }
}