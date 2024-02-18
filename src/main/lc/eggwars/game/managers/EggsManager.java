package lc.eggwars.game.managers;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;

import lc.eggwars.game.GameInProgress;
import lc.eggwars.teams.BaseTeam;
import lc.eggwars.utils.BlockLocation;

public final class EggsManager {

    public void setEggs(final GameInProgress map) {
        final Set<Entry<BaseTeam, BlockLocation>> eggs = map.getMapData().getEggs().entrySet();
    
        for (final Entry<BaseTeam, BlockLocation> egg : eggs) {
            final BlockLocation location = egg.getValue();
            map.getWorld().getBlockAt(location.x(), location.y(), location.z()).setType(Material.DRAGON_EGG);
        }
    }
}
