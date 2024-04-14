package lc.eggwars.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;

public final class BlockLocation {
    private final int x, y, z;

    public BlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public final String toString() {
        return x + "," + y + "," + z;
    }

    @Override
    public int hashCode() {
        return IntegerUtils.combineCords(x, y, z);
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof BlockLocation otherBlock)
            ? otherBlock.x == this.x && otherBlock.y == this.y && otherBlock.z == this.z
            : false;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public static BlockLocation create(String text) {
        final String[] split = StringUtils.split(text, ',');

        return new BlockLocation(
            Integer.parseInt(split[0]),
            Integer.parseInt(split[1]),
            Integer.parseInt(split[2]));
    }

    public Location toLocation(final World world) {
        return new Location(null, x, y, z);
    }

    public static BlockLocation toBlockLocation(final Location location) {
        return new BlockLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}