package lc.eggwars.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public interface ClickableBlock {
    void onClick(final Player player, final Action action);
    boolean supportLeftClick();
}
