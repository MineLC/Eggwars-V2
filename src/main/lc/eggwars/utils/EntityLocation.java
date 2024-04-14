package lc.eggwars.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;

public final class EntityLocation {
    private final int x, y, z;
    private final float yaw, pitch;

    public EntityLocation(int x, int y, int z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public final String toString() {
        return x + "," + y + "," + z + "," + yaw + "," + pitch;
    }

    @Override
    public int hashCode() {
        return IntegerUtils.combineCords(x, y, z);
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof EntityLocation otherEntity)
            ? otherEntity.x == this.x && otherEntity.y == this.y && otherEntity.z == this.z
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

    public float yaw() {
        return yaw;
    }
    
    public float pitch() {
        return pitch;
    }

    public static EntityLocation create(String text) {
        final String[] split = StringUtils.split(text, ',');

        return new EntityLocation(
            Integer.parseInt(split[0]),
            Integer.parseInt(split[1]),
            Integer.parseInt(split[2]),
            Float.parseFloat(split[3]),
            Float.parseFloat(split[4])
            );
    }

    public static EntityLocation toEntityLocation(final Location location, float yaw, float pitch) {
        return new EntityLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), yaw, pitch);
    }
}
