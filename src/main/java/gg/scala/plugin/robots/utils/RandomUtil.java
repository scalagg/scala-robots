package gg.scala.plugin.robots.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static int randInt(int min, int max) {
        int rand = -1;

        try {
            rand = ThreadLocalRandom.current().nextInt(min, max);
        } catch (Exception ignore) {
        }

        return rand;
    }

    public static double randDouble(double min, double max) {
        double rand = -1;

        try {
            rand = ThreadLocalRandom.current().nextDouble(min, max);
        } catch (Exception ignore) {
        }

        return rand;
    }
}
