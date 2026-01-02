package dev.recode.astro.api.rotation;

public final class RotationPoint {
    private float yaw;
    private float pitch;
    private int priority;
    private boolean instant;

    public RotationPoint(float yaw, float pitch, int priority, boolean instant) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.priority = priority;
        this.instant = instant;
    }
}