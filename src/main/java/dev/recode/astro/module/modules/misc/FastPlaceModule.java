package dev.recode.astro.module.modules.misc;

import dev.recode.astro.api.utils.OrbitManager;
import dev.recode.astro.api.event.events.ClientTickEvent;
import dev.recode.astro.api.event.orbit.EventHandler;
import dev.recode.astro.mixin.accessor.MinecraftAccessor;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.RangeSliderSetting;
import net.minecraft.client.Minecraft;

public class FastPlaceModule extends Module {
    private final RangeSliderSetting delay;

    public FastPlaceModule() {
        super("FastPlace", Category.MISC);
        setDescription("allows you to decrease the block place cooldown while holding down RMB");

        delay = new RangeSliderSetting("Delay", 0, 1, 0, 4);
        delay.setDescription("delay in between blocks (ticks)");

        addSetting(delay);
    }

    @Override
    public void onEnable() {
        OrbitManager.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        OrbitManager.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    public void onTick(ClientTickEvent event) {
        if (!isEnabled()) return;

        Minecraft client = Minecraft.getInstance();
        if (client == null) return;

        int currentDelay = ((MinecraftAccessor) client).getRightClickDelay();
        int maxDelay = (int) delay.getRandomDouble();

        if (currentDelay > maxDelay) {
            ((MinecraftAccessor) client).setRightClickDelay(maxDelay);
        }
    }
}
