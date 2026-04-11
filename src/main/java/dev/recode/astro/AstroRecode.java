package dev.recode.astro;

import dev.recode.astro.screens.menu.other.ConfigCFG;
import dev.recode.astro.api.event.events.ClientTickEvent;
import dev.recode.astro.api.utils.KeybindHandler;
import dev.recode.astro.api.utils.OrbitManager;
import dev.recode.astro.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstroRecode implements ModInitializer, ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("astrorecode");
    public static final String BRANCH = "Beta";
    public static final String VERSION = "0.1";
    public static final String NAME = "Astro(Recode)";


    @Override
    public void onInitialize() {
        LOGGER.info("Astro client (recode) {} {} loaded :D", BRANCH, VERSION);
        OrbitManager.initialize();
        ModuleManager.getInstance().registerAll();
        ConfigCFG.loadLatestConfig();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigCFG.saveLatestConfig());
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> OrbitManager.EVENT_BUS.post(new ClientTickEvent()));
        ClientTickEvents.END_CLIENT_TICK.register(KeybindHandler.getInstance()::tick);
    }
}