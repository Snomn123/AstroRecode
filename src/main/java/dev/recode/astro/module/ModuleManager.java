package dev.recode.astro.module;

import dev.recode.astro.module.modules.client.ClickGuiModule;
import dev.recode.astro.module.modules.client.ClientSpoofModule;
import dev.recode.astro.module.modules.combat.AimAssistModule;
import dev.recode.astro.module.modules.combat.ShieldStunModule;
import dev.recode.astro.module.modules.combat.TriggerBotModule;
import dev.recode.astro.module.modules.misc.BlinkModule;
import dev.recode.astro.module.modules.misc.FastPlaceModule;
import dev.recode.astro.module.modules.misc.NoJumpDelay;
import dev.recode.astro.module.modules.misc.StreamerModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private static ModuleManager instance;
    private final List<Module> modules = new ArrayList<>();

    public static ModuleManager getInstance() {
        if (instance == null) instance = new ModuleManager();
        return instance;
    }

    public void registerAll() {
        register(new ClickGuiModule());
        register(new ClientSpoofModule());

        register(new NoJumpDelay());
        register(new FastPlaceModule());
        register(new BlinkModule());
        register(new StreamerModule());

        register(new TriggerBotModule());
        register(new ShieldStunModule());
        register(new AimAssistModule());
    }

    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Module getModuleByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        return modules.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}