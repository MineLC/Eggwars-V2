package lc.eggwars.others.sidebar;

import java.util.Collection;

import org.bukkit.entity.Player;

public interface EggwarsSidebar {
    void send(final Player player);
    void send(final Collection<Player> players);
}