package dev.recode.astro.module.modules.misc;

import dev.recode.astro.OrbitManager;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;

public class NoJumpDelay extends Module {

    public NoJumpDelay() {
        super("NoJumpDelay", Category.MISC);
        setDescription("Removes the delay between jumps");
    }

    @Override
    public void onEnable() {
        OrbitManager.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        OrbitManager.EVENT_BUS.unsubscribe(this);
    }
}