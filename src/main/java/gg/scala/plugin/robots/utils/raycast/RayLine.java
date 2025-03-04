package gg.scala.plugin.robots.utils.raycast;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RayLine {

    private double min;
    private double max;
    private double lineSize;

    public RayLine(double min,double max,double lineSize) {
        this.min = min;
        this.max = max;
        this.lineSize = lineSize;
    }

    public double ratioMin() {
        return min / lineSize;
    }

    public double ratioMax() {
        return max / lineSize;
    }
}
