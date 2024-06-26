package lc.eggwars.others.sidebar;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.sidebar.types.GameSidebar;
import lc.eggwars.others.sidebar.types.PregameSidebar;
import lc.eggwars.others.sidebar.types.SpawnSidebar;

public final class StartSidebar {

    private final FileConfiguration config;

    public StartSidebar(EggwarsPlugin plugin) {
        this.config = plugin.loadConfig("sidebars");
    }

    public void load() {
        final EggwarsSidebar[] sidebars = new EggwarsSidebar[3];

        sidebars[SidebarType.SPAWN.ordinal()] = createSidebar(config, "spawn", SidebarType.SPAWN);
        sidebars[SidebarType.PREGAME.ordinal()] = createSidebar(config, "pregame", SidebarType.PREGAME);
        sidebars[SidebarType.IN_GAME.ordinal()] = createSidebar(config, "ingame", SidebarType.IN_GAME);

        SidebarStorage.update(new SidebarStorage(sidebars));
    }

    private EggwarsSidebar createSidebar(final FileConfiguration config, final String path, final SidebarType type) {
        final String sidebarPath = path + '.';
        final String[] lines = toArray(config.getStringList(sidebarPath + "lines")); 
        final String title = Messages.color(config.getString(sidebarPath + "title"));
    
        final EggwarsSidebar sidebar = getSidebar(type, lines, title);

        return sidebar;
    }

    private String[] toArray(final List<String> list) {
        if (list.isEmpty()) {
            return new String[] { "" };
        }
        final String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = Messages.color(list.get(i));
        }
        return array;
    }

    private EggwarsSidebar getSidebar(final SidebarType type, final String[] lines, final String title) {
        switch (type) {
            case SPAWN: return new SpawnSidebar(lines, title);
            case PREGAME: return new PregameSidebar(lines, title, Messages.color(config.getString("team")), Messages.color(config.getString("solo")));
            case IN_GAME: return new GameSidebar(title);
            default: return null;
        }
    }
}