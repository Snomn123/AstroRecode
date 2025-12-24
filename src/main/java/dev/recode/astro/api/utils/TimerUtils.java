package dev.recode.astro.api.utils;

public class TimerUtils {
    private long lastMS = 0L;

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean delay(long delay) {
        return System.currentTimeMillis() - lastMS >= delay;
    }

    public boolean delay(double delay) {
        return System.currentTimeMillis() - lastMS >= (long) delay;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setLastMS(long lastMS) {
        this.lastMS = lastMS;
    }

    public long getLastMS() {
        return lastMS;
    }
}