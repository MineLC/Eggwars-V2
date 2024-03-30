package lc.eggwars.listeners.pvp;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameDeath;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.eggwars.game.PlayerInGame;
import lc.eggwars.game.managers.ShopKeeperManager;
import lc.eggwars.others.deaths.DeathStorage;
import lc.eggwars.others.events.GameEventType;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.others.levels.LevelStorage;
import lc.eggwars.teams.GameTeam;
import lc.eggwars.utils.BlockLocation;

public final class PlayerRespawnListener implements EventListener {

    private final EggwarsPlugin plugin;

    public PlayerRespawnListener(EggwarsPlugin plugin) {
        this.plugin = plugin;
    }

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerRespawnEvent.class
    )
    public void handle(Event defaultEvent) {
        final PlayerRespawnEvent event = (PlayerRespawnEvent)defaultEvent;
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());

        if (playerInGame == null) {
            return;
        }
        final GameInProgress game = playerInGame.getGame();
        final Player player = event.getPlayer();
        final GameTeam team = game.getTeamPerPlayer().get(player);
        if (team == null) {
            return;
        }

        event.setRespawnLocation(player.getWorld().getSpawnLocation());
        player.setGameMode(GameMode.SPECTATOR);

        if (player.getKiller() != null) {
            if (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.FULLHEALTH) {
                player.getKiller().setHealth(20.0D);
                player.getKiller().setFoodLevel(20);
                player.getKiller().playSound(player.getLocation(), Sound.EAT, 1.0f, 1.0f);       
            } else {
                player.getKiller().playSound(player.getLocation(), Sound.BAT_DEATH, 1.0f, 1.0f);       
            }
        }
        
        if (!team.hasEgg()) {
            new GameDeath(plugin).death(playerInGame, team, player, false, true);
            return;
        }

        final BlockLocation spawn = game.getMapData().getSpawns().get(team.getBase());
        final Location spawnLocation = new Location(player.getWorld(), spawn.x(), spawn.y(), spawn.z());
       
        DeathStorage.getStorage().onDeath(playerInGame, game.getPlayers(), player, () -> {
            LevelStorage.getStorage().onDeath(player, false);
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.SURVIVAL);
            KitStorage.getStorage().setKit(player, false);
            new ShopKeeperManager().send(player, game);
        }, false);
    }
}