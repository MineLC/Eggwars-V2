package lc.eggwars.listeners.pvp;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.others.deaths.DeathStorage;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.levels.LevelManager;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

public final class PlayerRespawnListener implements EventListener {

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerRespawnEvent.class
    )
    public void handle(Event defaultEvent) {
        final PlayerRespawnEvent event = (PlayerRespawnEvent)defaultEvent;
        final GameInProgress game = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());
        if (game == null) {
            return;
        }
        final Player player = event.getPlayer();
        final BaseTeam team = game.getTeamPerPlayer().get(player);
        if (team == null) {
            return;
        }

        if (game.getTeamsWithEgg().contains(team)) {
            final BlockLocation spawn = game.getMapData().getSpawns().get(team);
            final Location spawnLocation = new Location(player.getWorld(), spawn.x(), spawn.y(), spawn.z());

            DeathStorage.getStorage().onDeath(game.getPlayers(), player, () -> {
                new LevelManager().onDeath(player, false);
                player.teleport(spawnLocation);
                player.setGameMode(GameMode.SURVIVAL);
                KitStorage.getStorage().setKit(player, false);
            }, false);
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getWorld().getSpawnLocation());

        DeathStorage.getStorage().onDeath(game.getPlayers(), player, () ->
            new LevelManager().onDeath(player, true),
            true);

        game.getPlayersLiving().remove(player);
    }
}