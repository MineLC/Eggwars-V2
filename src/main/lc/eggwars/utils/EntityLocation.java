package lc.eggwars.utils;

import org.bukkit.Location;

public final class EntityLocation {
    private final int x, y, z;
    private final float yaw;

    public EntityLocation(int x, int y, int z, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }

    @Override
    public final String toString() {
        return x + "," + y + "," + z + "," + yaw;
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

    public static EntityLocation create(String text) {
        final String[] split = text.split(",");

        return new EntityLocation(
            Integer.parseInt(split[0]),
            Integer.parseInt(split[1]),
            Integer.parseInt(split[2]),
            Float.parseFloat(split[3]));
    }

    public static EntityLocation toEntityLocation(final Location location, float yaw) {
        return new EntityLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), yaw);
    }
}
