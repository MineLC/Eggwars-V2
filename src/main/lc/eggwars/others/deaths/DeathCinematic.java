package lc.eggwars.others.deaths;

import org.bukkit.Bukkit;
import org.bukkit.Title;
import org.bukkit.entity.Player;

import lc.eggwars.game.countdown.CountdownCallback;
import net.md_5.bungee.api.chat.TextComponent;

final class DeathCinematic implements Runnable {

    private final CountdownCallback complete;
    private final String respawnTitle, respawnSubtitle;

    private final Player player;

    private int seconds, id;

    DeathCinematic(CountdownCallback complete, String respawnTitle, String respawnSubtitle, int respawnWaitTime, Player player) {
        this.complete = complete;
        this.respawnTitle = respawnTitle;
        this.respawnSubtitle = respawnSubtitle;
        this.seconds = respawnWaitTime;
        this.player = player;
    }

    @Override
    public void run() {
        if (seconds == 0) {
            complete.execute();
            Bukkit.getScheduler().cancelTask(id);
            return;
        }
        final String subtitle = respawnSubtitle.replace("%time%", parseTime(seconds));
        final Title titleOptions = new Title(TextComponent.fromLegacyText(respawnTitle), TextComponent.fromLegacyText(subtitle), 0, 25, 0);
        player.sendTitle(titleOptions);
        --seconds;
    }

    private String parseTime(final int seconds) {
        if (seconds >= 60) {
            int minutes = seconds / 60;
            return minutes + " minuto" + (minutes > 1 ? 's' : ' ');
        }
        return seconds + " segundo" + (seconds > 1 ? 's' : ' ');
    }

    void setId(int id) {
        this.id = id;
    }
}