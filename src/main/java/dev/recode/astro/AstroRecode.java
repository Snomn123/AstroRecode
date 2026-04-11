package dev.recode.astro;

import dev.recode.astro.api.config.ConfigCFG;
import dev.recode.astro.api.event.events.ClientTickEvent;
import dev.recode.astro.api.registry.ModuleRegistry;
import dev.recode.astro.api.utils.OrbitManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstroRecode implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("astrorecode");
    public static final String BRANCH = "Beta";
    public static final String VERSION = "0.1";
    public static final String NAME = "Astro(Recode)";

    @Override
    public void onInitialize() {
        LOGGER.info("Astro client (recode) {} {} loaded :D", BRANCH, VERSION);
        OrbitManager.initialize();
        ModuleRegistry.registerModules();
        ConfigCFG.loadLatestConfig();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            OrbitManager.EVENT_BUS.post(new ClientTickEvent());
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            ConfigCFG.saveLatestConfig();
        });
    }
}
