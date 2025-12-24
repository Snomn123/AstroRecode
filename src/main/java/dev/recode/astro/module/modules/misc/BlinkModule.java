package dev.recode.astro.module.modules.misc;

import dev.recode.astro.OrbitManager;
import dev.recode.astro.api.event.orbit.EventHandler;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.api.event.events.PacketSendEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkModule extends Module {
    private final ConcurrentLinkedQueue<Packet<?>> outgoingQueue = new ConcurrentLinkedQueue<>();

    private boolean releasing = false;

    public BlinkModule() {
        super("Blink", Category.MISC);
        setDescription("Chokes outgoing packets creating a lag effect");
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            toggle();
            return;
        }

        OrbitManager.getEventBus().subscribe(this);
        releasing = false;
        outgoingQueue.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        OrbitManager.getEventBus().unsubscribe(this);
        release();
        super.onDisable();
    }

    public void onUpdate() {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        // once  its reached packets it sends
        if (outgoingQueue.size() >= 30) {
            release();
        }
    }

    @EventHandler
    public void onPacketSend(PacketSendEvent event) {
        if (!isEnabled()) return;
        if (releasing) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }


        if (event.packet instanceof ServerboundKeepAlivePacket) {
            return;
        }

        // let critical packets through to avoid desync
        if (event.packet instanceof ServerboundClientCommandPacket ||
                event.packet instanceof ServerboundChatPacket ||
                event.packet instanceof ServerboundResourcePackPacket ||
                event.packet instanceof ServerboundPongPacket) {
            return;
        }

        // queue packet and stop
        outgoingQueue.add(event.packet);
        event.cancel();
    }

    public void release() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() == null) {
            outgoingQueue.clear();
            return;
        }

        releasing = true;

        while (!outgoingQueue.isEmpty()) {
            Packet<?> packet = outgoingQueue.poll();
            if (packet != null) {
                mc.getConnection().send(packet);
            }
        }

        releasing = false;
    }
}