package lc.eggwars.game.clickable;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import lc.eggwars.database.PlayerDataStorage;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.game.GameStorage;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.SidebarStorage;
import lc.eggwars.others.sidebar.SidebarType;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.ClickableBlock;

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
        if (game == null || game.getState() != GameState.IN_GAME) {
            return;
        }

        if (game.playerIsDead(player)) {
            return;
        }

        final GameTeam playerTeam = game.getTeamPerPlayer().get(player);
        if (playerTeam == null) {
            return;
        }
        if (team.equals(playerTeam.getBase())) {
            Messages.send(player, "eggs.break-it-egg");
            return;
        }

        final GameTeam teamToDestroy = game.getTeamPerBase().get(team);
        if (teamToDestroy == null || !teamToDestroy.hasEgg()) {
            return;
        }

        player.getWorld().getBlockAt(location.x(), location.y(), location.z()).setType(Material.AIR);
        teamToDestroy.destroyEgg();

        final String eggBreaked = Messages.get("eggs.break-other").replace("%team%", team.getName()).replace("%player%", player.getName());
        Messages.sendNoGet(game.getPlayers(), eggBreaked);

        PlayerDataStorage.getStorage().get(player.getUniqueId()).destroyedEggs++;
        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(game.getPlayers());
        makeSound(game.getTeamPerBase().get(team).getPlayers());
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0f, 1.0f);
    }

    private void makeSound(final Set<Player> players) {
        for (final Player player : players) {
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean supportLeftClick() {
        return true;
    }
}