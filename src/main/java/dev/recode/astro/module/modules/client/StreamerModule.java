package dev.recode.astro.module.modules.client;

import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.*;
import net.minecraft.client.Minecraft;

public class StreamerModule extends Module {

    private static StreamerModule instance;

    public final StringSetting replacementName;


    public StreamerModule() {
        super("Streamer", Category.CLIENT);
        setDescription("hides/spoofs your name");

        instance = this;

        replacementName = new StringSetting("hides/spoofs your name", "AstroRecodeUser");
        addSetting(replacementName);
    }

    public static StreamerModule getInstance() {
        return instance;
    }

    public String hiddenUser(String text) {
        if (Minecraft.getInstance().player == null) return text;

        String username = Minecraft.getInstance().player.getName().getString();
        String replacement = replacementName.getValue();

        return text.replace(username, replacement);
    }
}