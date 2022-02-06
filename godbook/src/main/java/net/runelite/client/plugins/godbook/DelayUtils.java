package net.runelite.client.plugins.godbook;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DelayUtils {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static final Random random = new Random();

    /**
     * Pauses execution for a random amount of time between two values.
     *
     * @param minSleep The minimum time to sleep.
     * @param maxSleep The maximum time to sleep.
     * @see #sleep(int)
     * @see #random(int, int)
     */
    public void sleep(int minSleep, int maxSleep) {
        sleep(random(minSleep, maxSleep));
    }

    /**
     * Pauses execution for a given number of milliseconds.
     *
     * @param toSleep The time to sleep in milliseconds.
     */
    public void sleep(int toSleep) {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(toSleep);

            // Guarantee minimum sleep
            long now;
            while (start + toSleep > (now = System.currentTimeMillis())) {
                Thread.sleep(start + toSleep - now);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long toSleep) {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(toSleep);

            // Guarantee minimum sleep
            long now;
            while (start + toSleep > (now = System.currentTimeMillis())) {
                Thread.sleep(start + toSleep - now);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delayKey(int key, long delay) {
        executorService.submit(() -> {
            sleep(delay);
            pressKey(key);
        });
    }

    private void pressKey(int key){
        try {
            Robot robot = new Robot();
            robot.keyPress(key);
        } catch (AWTException ignored){
        }
    }

    /* No bias */
    public double nextDouble(int min, int max) {
        return min + (random.nextDouble() * ((max-min) + 1));
    }

    public long nextLong(int min, int max) {
        return (long) nextDouble(min, max);
    }

    public int nextInt(int min, int max) {
        return (int) nextDouble(min, max);
    }

    //Ganom's function, generates a random number allowing for curve and weight
    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        if (weightedDistribution) {
            /* generate a gaussian random (average at 0.0, std dev of 1.0)
             * take the absolute value of it (if we don't, every negative value will be clamped at the minimum value)
             * get the log base e of it to make it shifted towards the right side
             * invert it to shift the distribution to the other end
             * clamp it to min max, any values outside of range are set to min or max */
            return (long) clamp((-Math.log(Math.abs(random.nextGaussian()))) * deviation + target, min, max);
        } else {
            /* generate a normal even distribution random */
            return (long) clamp(Math.round(random.nextGaussian() * deviation + target), min, max);
        }
    }

    private double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Returns a random double with min as the inclusive lower bound and max as
     * the exclusive upper bound.
     *
     * @param min The inclusive lower bound.
     * @param max The exclusive upper bound.
     * @return Random double min <= n < max.
     */
    public static double random(double min, double max) {
        return Math.min(min, max) + random.nextDouble() * Math.abs(max - min);
    }

    /**
     * Returns a random integer with min as the inclusive lower bound and max as
     * the exclusive upper bound.
     *
     * @param min The inclusive lower bound.
     * @param max The exclusive upper bound.
     * @return Random integer min <= n < max.
     */
    public static int random(int min, int max) {
        int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
    }
}
