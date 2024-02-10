package lc.eggwars.mapsystem.manager;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;

import lc.eggwars.mapsystem.GameMap;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

public final class EggsManager {

    public void setEggs(final GameMap map) {
        final Set<Entry<BaseTeam, BlockLocation>> eggs = map.getEggs().entrySet();
    
        for (final Entry<BaseTeam, BlockLocation> egg : eggs) {
            final BlockLocation location = egg.getValue();
            map.getWorld().getBlockAt(location.x(), location.y(), location.z()).setType(Material.DRAGON_EGG);
        }
    }
}
