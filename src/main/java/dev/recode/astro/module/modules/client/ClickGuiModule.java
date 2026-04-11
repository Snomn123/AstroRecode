package dev.recode.astro.module.modules.client;

import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.ColorSetting;
import dev.recode.astro.screens.menu.ClickGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ClickGuiModule extends Module {

    private static final int MODULE_COLOR = 0xFF6969FF;

    public final ColorSetting primaryColor;
    public final ColorSetting secondaryColor;
    public final ColorSetting backgroundColor;

    public ClickGuiModule() {
        super("ClickGUI", Category.CLIENT, GLFW.GLFW_KEY_RIGHT_SHIFT);
        setDescription("shows this menu");

        primaryColor = new ColorSetting("Primary", 0xFF6969FF, MODULE_COLOR);
        primaryColor.setDescription("Primary/main color");

        secondaryColor = new ColorSetting("Secondary", 0xFF303030, MODULE_COLOR);
        secondaryColor.setDescription("Secondary color");

        backgroundColor = new ColorSetting("Background", 0xFF202020, MODULE_COLOR);
        backgroundColor.setDescription("Background color");

        addSetting(primaryColor);
        addSetting(secondaryColor);
        addSetting(backgroundColor);
    }

    @Override
    public void onEnable() {
        if (Minecraft.getInstance().player == null) {
            setEnabled(false);
            return;
        }

        ClickGui guiScreen = new ClickGui();

        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) -> {
            if (screen == guiScreen) {
                ScreenEvents.remove(screen).register(s -> setEnabled(false));
            }
        });

        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(guiScreen));
    }
}