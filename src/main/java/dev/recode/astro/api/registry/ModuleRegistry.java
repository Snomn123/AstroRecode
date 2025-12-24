package dev.recode.astro.api.registry;

import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import dev.recode.astro.module.modules.client.ClientSpoofModule;
import dev.recode.astro.module.modules.client.SettingRenderTestModule;
import dev.recode.astro.module.modules.client.StreamerModule;
import dev.recode.astro.module.modules.combat.ShieldStunModule;
import dev.recode.astro.module.modules.combat.TriggerBotModule;
import dev.recode.astro.module.modules.misc.*;


public class ModuleRegistry {
    private static ClickGuiModule clickGUIModule;

    public static void registerModules() {
        ModuleManager manager = ModuleManager.getInstance();




        // client
        //manager.register(new SettingRenderTestModule());
        manager.register(new ClickGuiModule());
        manager.register(new ClientSpoofModule());
        manager.register(new StreamerModule());


        // misc
        manager.register(new NoJumpDelay());
        manager.register(new FastPlaceModule());
        manager.register(new BlinkModule());



        // combat
        manager.register(new TriggerBotModule());
        manager.register(new ShieldStunModule());

    }

    public static ClickGuiModule getClickGUIModule() {
        return clickGUIModule;
    }
}