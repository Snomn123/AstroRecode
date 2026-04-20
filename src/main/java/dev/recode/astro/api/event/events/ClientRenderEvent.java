package dev.recode.astro.api.event.events;

import net.minecraft.client.DeltaTracker;

public class ClientRenderEvent {
    private final DeltaTracker deltaTracker;
    private final boolean ticking;

    public ClientRenderEvent(DeltaTracker deltaTracker, boolean ticking) {
        this.deltaTracker = deltaTracker;
        this.ticking = ticking;
    }

    public DeltaTracker deltaTracker() { return deltaTracker; }

    public boolean ticking() { return ticking; }

    public float partialTick() { return deltaTracker.getGameTimeDeltaPartialTick(true); }
}