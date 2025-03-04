package gg.scala.plugin.robots.utils.objects;

import org.bukkit.util.Vector;

public class AABB implements Cloneable {

    private Vector min;
    private Vector max;

    public AABB(Vector min, Vector max) {
        this.min = min;
        this.max = max;
    }

    public void translate(Vector vector) {
        min.add(vector);
        max.add(vector);
    }

    //translate AABB so that the min point is located at the given vector (AABB origin is min)
    public void translateTo(Vector vector) {
        max.setX(vector.getX() + (max.getX() - min.getX()));
        max.setY(vector.getY() + (max.getY() - min.getY()));
        max.setZ(vector.getZ() + (max.getZ() - min.getZ()));
        min.setX(vector.getX());
        min.setY(vector.getY());
        min.setZ(vector.getZ());
    }

    public boolean isColliding(AABB other) {
        if (max.getX() < other.getMin().getX() || min.getX() > other.getMax().getX()) {
            return false;
        }
        if (max.getY() < other.getMin().getY() || min.getY() > other.getMax().getY()) {
            return false;
        }
        return !(max.getZ() < other.getMin().getZ()) && !(min.getZ() > other.getMax().getZ());
    }

    public boolean isColliding(Vector loc) {
        double minX = (min.getX());
        double minY = (min.getY());
        double minZ = (min.getZ());

        double maxX = (max.getX());
        double maxY = (max.getY());
        double maxZ = (max.getZ());

        double x = (loc.getX());
        double y = (loc.getY());
        double z = (loc.getZ());

        return minX < x && x < maxX && minY < y && y < maxY && minZ < z && z < maxZ;
    }

    public AABB clone() {
        AABB clone;
        try {
            clone = (AABB) super.clone();
            clone.min = this.min.clone();
            clone.max = this.max.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vector getMax() {
        return max;
    }

    public Vector getMin() {
        return min;
    }
}
