package lc.eggwars.listeners.pvp;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.deaths.StartDeaths;
import lc.eggwars.others.kits.KitStorage;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

public final class PlayerRespawnListener implements EventListener {

    private final EggwarsPlugin plugin;
    private final String title, subtitle;
    private final String finalKillPrefix, suffixIfPlayerKill;
    private final String[] deathMessages;
    private final int waitTime;

    public PlayerRespawnListener(EggwarsPlugin plugin, StartDeaths deaths) {
        this.plugin = plugin;
        this.title = Messages.color(plugin.getConfig().getString("respawn.title"));
        this.subtitle = Messages.color(plugin.getConfig().getString("respawn.subtitle"));
        this.waitTime = plugin.getConfig().getInt("respawn.seconds");
        this.finalKillPrefix = deaths.get("final-death-prefix");
        this.suffixIfPlayerKill = deaths.get("suffix-if-killer-exist");
        this.deathMessages = deaths.load(plugin);
    }

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerRespawnEvent.class
    )
    public void handle(Event defaultEvent) {
        final PlayerRespawnEvent event = (PlayerRespawnEvent)defaultEvent;
        final Player player = event.getPlayer();
        final GameInProgress map = GameStorage.getStorage().getGame(player.getUniqueId());

        if (map == null) {
            return;
        }

        final BaseTeam team = map.getTeamPerPlayer().get(player);

        if (team == null) {
            return;
        }


        if (map.getTeamsWithEgg().contains(team)) {
            final BlockLocation spawn = map.getMapData().getSpawns().get(team);
            final Location spawnLocation = new Location(player.getWorld(), spawn.x(), spawn.y(), spawn.z());
            final DeathCinematic cinematic = new DeathCinematic(player, title, subtitle, spawnLocation, waitTime);
            cinematic.setId(plugin.getServer().getScheduler().runTaskTimer(plugin, cinematic, 0, 20).getTaskId());
            sendDeathMessage(map, player, false);
            return;
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(map.getWorld().getSpawnLocation());
        GameStorage.getStorage().finalKill(map, team, player);
        sendDeathMessage(map, player, true);
    }

    private void sendDeathMessage(final GameInProgress game, final Player player, boolean finalKill) {
        final String deathMessage = deathMessages[player.getLastDamageCause().getCause().ordinal()];
        if (deathMessage == null) {
            return;
        }
        String finalMessage = (finalKill) ? finalKillPrefix : "";

        finalMessage += deathMessage.replace("%v%", player.getName());
        if (player.getKiller() != null) {
            finalMessage = finalMessage.replace("%d%", player.getKiller().getName()) + suffixIfPlayerKill;
        }

        Messages.sendNoGet(game.getPlayers(), finalMessage);
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
                KitStorage.getStorage().setKit(player, false);
                Bukkit.getScheduler().cancelTask(id);
                return;
            }
            final Title titleOptions = new Title(title.replace("%seconds%", String.valueOf(seconds)));
            player.sendTitle(titleOptions, subtitle);
            --seconds;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}