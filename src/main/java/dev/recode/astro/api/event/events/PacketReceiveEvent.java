package dev.recode.astro.api.event.events;

import net.minecraft.network.protocol.Packet;

public class PacketReceiveEvent {
    public final Packet<?> packet;
    private boolean cancelled = false;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}