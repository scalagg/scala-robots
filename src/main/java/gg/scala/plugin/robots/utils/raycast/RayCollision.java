package gg.scala.plugin.robots.utils.raycast;

import lombok.Getter;
import gg.scala.plugin.robots.utils.objects.AABB;
import org.bukkit.util.Vector;

@Getter
public class RayCollision {

    private final Vector from;
    private final Vector to;
    private final AABB collision;

    public RayCollision(Vector from,Vector to,AABB collision) {
        this.from = from;
        this.to = to;
        this.collision = collision;
    }

    public Vector collides() {
        Vector collMin = collision.getMin();
        Vector collMax = collision.getMax();

        RayLine lineX = getRayLine(from.getX(),to.getX(),collMin.getX(),collMax.getX());
        RayLine lineY = getRayLine(from.getY(),to.getY(),collMin.getY(),collMax.getY());
        RayLine lineZ = getRayLine(from.getZ(),to.getZ(),collMin.getZ(),collMax.getZ());

        if (lineX == null || lineY == null || lineZ == null) return null;

        double ratioMinMeetingPoint = max(lineX.ratioMin(), lineY.ratioMin(),lineZ.ratioMin());
        double ratioMaxMeetingPoint = min(lineX.ratioMax(), lineY.ratioMax(),lineZ.ratioMax());

        double meetingPointX = ratioMinMeetingPoint * lineX.getLineSize();
        double meetingPointY = ratioMinMeetingPoint * lineY.getLineSize();
        double meetingPointZ = ratioMinMeetingPoint * lineZ.getLineSize();

        return new Vector(meetingPointX,meetingPointY,meetingPointZ);
    }

    public RayLine getRayLine(double rayVar1,double rayVar2,double collMin,Double collMax) {
        double rayMin = Math.min(rayVar1,rayVar2);
        double rayMax = Math.max(rayVar1,rayVar2);

        double collidesMin = Math.max(rayMin,collMin);
        double collidesMax = Math.min(rayMax,collMax);

        if (collidesMin > collidesMax) return null;

        if (rayMin != rayVar1) {
            double clonedMin = collidesMin;
            collidesMin = collidesMax;
            collidesMax = clonedMin;
        }
        return new RayLine(collidesMin,collidesMax,rayMax - rayMin);
    }

    public double max(double var1,double var2,double var3) {
        double var4 = Math.max(var1,var2);
        return Math.max(var3,var4);
    }

    public double min(double var1,double var2,double var3) {
        double var4 = Math.min(var1,var2);
        return Math.min(var3,var4);
    }
}
