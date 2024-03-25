package lc.eggwars.others.events.special;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;

public final class PotionEvent {

    private final PotionEffect[] effects;
    private final GameInProgress game;

    public PotionEvent(PotionEffect[] effects, GameInProgress game) {
        this.game = game;
        this.effects = effects;
    }

    public void execute(EggwarsPlugin plugin) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            final Set<Player> players = game.getPlayers();
            for (final Player player : players) {
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }
                for (final PotionEffect effect : effects) {
                    player.addPotionEffect(effect);
                }
            }
        });
    }

    public static record Data(
        PotionEffect[] fatigueEvent,
        PotionEffect[] willpower
    ) {}
}