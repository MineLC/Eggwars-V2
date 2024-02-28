package lc.eggwars.others.sidebar;

public final class SidebarStorage {
    private static SidebarStorage storage;

    private final EggwarsSidebar[] sidebars;

    SidebarStorage(EggwarsSidebar[] sidebars) {
        this.sidebars = sidebars;
    }

    public EggwarsSidebar getSidebar(final SidebarType type){
        return sidebars[type.ordinal()];
    }

    public static SidebarStorage getStorage() {
        return storage;
    }

    static void update(final SidebarStorage newStorage) {
        storage = newStorage;
    }
}
