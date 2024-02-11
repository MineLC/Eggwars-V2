package lc.eggwars.listeners.pvp;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameStorage;
import lc.eggwars.listeners.internal.EventListener;
import lc.eggwars.listeners.internal.ListenerData;
import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;
import lc.eggwars.utils.Chat;

public final class PlayerRespawnListener implements EventListener {

    private final EggwarsPlugin plugin;
    private final String title, subtitle;
    private final int waitTime;

    public PlayerRespawnListener(EggwarsPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.title = Chat.color(config.getString("respawn.title"));
        this.subtitle = Chat.color(config.getString("respawn.subtitle"));
        this.waitTime = config.getInt("respawn.seconds");
    }

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerRespawnEvent.class
    )
    public void handle(Event defaultEvent) {
        final PlayerRespawnEvent event = (PlayerRespawnEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameMap map = GameStorage.getStorage().getGame(player.getUniqueId());

        if (map == null) {
            return;
        }

        final BaseTeam team = map.getTeamPerPlayer().get(player);

        if (team == null) {
            return;
        }

        // TODO - Add death messages

        if (map.getTeamsWithEgg().contains(team)) {
            final BlockLocation spawn = map.getSpawn(team);
            final Location spawnLocation = new Location(player.getWorld(), spawn.x(), spawn.y(), spawn.z());
            final DeathCinematic cinematic = new DeathCinematic(player, title, subtitle, spawnLocation, waitTime);
            cinematic.setId(plugin.getServer().getScheduler().runTaskTimer(plugin, cinematic, 0, 20).getTaskId());
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("Esta es tu muerte definitiva");
        player.teleport(map.getWorld().getSpawnLocation());

        GameStorage.getStorage().finalKill(map, team, player);
    }

    private static final class DeathCinematic implements Runnable {
        private final Player player;
        private final String title, subtitle;
        private final Location spawn;
        private int id;
        private int seconds;

        private DeathCinematic(Player player, String title, String subtitle, Location spawn, int waitTime) {
            this.player = player;
            this.title = title;
            this.subtitle = subtitle;
            this.spawn = spawn;
            this.seconds = waitTime;
        }

        @Override
        public void run() {
            if (seconds == 0) {
                player.teleport(spawn);
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage("Has muerto, pero reapareceras en el spawn de tu equipo");
                Bukkit.getScheduler().cancelTask(id);
                return;
            }
            player.sendTitle(title.replace("%seconds%", String.valueOf(seconds)), subtitle);
            --seconds;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}