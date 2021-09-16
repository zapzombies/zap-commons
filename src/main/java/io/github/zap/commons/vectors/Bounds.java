package io.github.zap.commons.vectors;

import org.jetbrains.annotations.NotNull;

public final class Bounds {
    private final double minX;
    private final double minY;
    private final double minZ;

    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public Bounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double minZ() {
        return minZ;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double maxZ() {
        return maxZ;
    }

    public @NotNull Vector3D min() {
        return Vectors.of(minX, minY, minZ);
    }

    public @NotNull Vector3D max() {
        return Vectors.of(maxX, maxY, maxZ);
    }


    public boolean overlaps(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }

    public boolean overlaps(@NotNull Bounds other) {
        return overlaps(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public static @NotNull Bounds positionDirectional(@NotNull Direction direction, double minX, double minY,
                                                        double minZ, double maxX, double maxY, double maxZ) {
        double x;
        double y;
        double z;

        double x2;
        double y2;
        double z2;

        if(direction.x() < 0) {
            x = x2 = minX;
        }
        else if(direction.x() == 0) {
            x = minX;
            x2 = maxX;
        }
        else {
            x = x2 = maxX;
        }

        if(direction.y() < 0) {
            y = y2 = minY;
        }
        else if(direction.y() == 0) {
            y = minY;
            y2 = maxY;
        }
        else {
            y = y2 = maxY;
        }

        if(direction.z() < 0) {
            z = z2 = minZ;
        }
        else if(direction.z() == 0) {
            z = minZ;
            z2 = maxZ;
        }
        else {
            z = z2 = maxZ;
        }

        return new Bounds(x, y, z, x2, y2, z2);
    }

    public @NotNull Bounds positionDirectional(@NotNull Direction direction) {
        return positionDirectional(direction, minX, minY, minZ, maxX, maxY, maxZ);
    }
}
