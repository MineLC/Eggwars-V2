package lc.eggwars.game.countdown.pregame;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.netty.util.collection.IntObjectHashMap;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.shop.metadata.LeatherArmorColorMetadata;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.teams.GameTeam;

public final class PreGameTemporaryData {
    private final Inventory teamSelector;
    private final IntObjectHashMap<BaseTeam> teamInventorySlots;

    public PreGameTemporaryData(Inventory teamSelector, IntObjectHashMap<BaseTeam> teamInventorySlots) {
        this.teamSelector = teamSelector;
        this.teamInventorySlots = teamInventorySlots;
    }

    public void joinToTeam(final GameTeam playerTeam, final Player player, final GameInProgress game, final BaseTeam team) {
        GameTeam teamToJoin = game.getTeamPerBase().get(team);

        if (teamToJoin == null) {
            teamToJoin = new GameTeam(team);
            game.getTeams().add(teamToJoin);
            game.getTeamPerBase().put(team, teamToJoin);
        }

        game.getTeamPerPlayer().put(player, teamToJoin);
        teamToJoin.add(player);

        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        new LeatherArmorColorMetadata().setColor(chestplate, player, team.getLeatherColor());

        player.getInventory().setChestplate(chestplate);
    }

    public void leave(final Player player, final GameInProgress game) {
        final GameTeam team = game.getTeamPerPlayer().get(player);
        if (team != null) {
            team.remove(player);
        }
        game.getTeamPerPlayer().remove(player);
    }

    public Inventory getTeamSelectorInventory() {
        return teamSelector;
    }

    public BaseTeam getTeam(int clickedSlot) {
        return teamInventorySlots.get(clickedSlot);
    }
}