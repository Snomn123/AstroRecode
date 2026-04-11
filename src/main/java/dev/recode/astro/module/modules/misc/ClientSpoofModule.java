package dev.recode.astro.module.modules.misc;

import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.ModeSetting;

import java.util.Arrays;

public class ClientSpoofModule extends Module {

    private final ModeSetting clientMode;

    public ClientSpoofModule() {
        super("ClientSpoof", Category.MISC);
        setDescription("spoofs client brand");

        clientMode = new ModeSetting("mode", Arrays.asList("Vanilla", "Forge", "Lunar", "Badlion"), 0);
        addSetting(clientMode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public String getSpoofedBrand() {
        String mode = clientMode.getMode();
        switch (mode) {
            case "Forge":
                return "fml,forge";
            case "Lunar":
                return "Lunar";
            case "Badlion":
                return "Badlion";
            case "Vanilla":
            default:
                return "vanilla";
        }
    }
}