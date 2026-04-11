package dev.recode.astro.screens.menu.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.Setting;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import dev.recode.astro.module.settings.KeybindSetting;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConfigCFG {
    private static final Path CONFIG_DIR = Path.of("astro/cfg/config");
    private static final Path LATEST_FILE = CONFIG_DIR.resolve("latest.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ImString newConfigName = new ImString(69);

    static {
        newConfigName.set("CoolConfig");
    }

    public static void saveConfig(String name) {
        try {
            if (name == null || name.trim().isEmpty()) return;
            Files.createDirectories(CONFIG_DIR);
            Path file = CONFIG_DIR.resolve(name.trim() + ".json");
            JsonObject config = new JsonObject();
            for (Module mod : ModuleManager.getInstance().getModules()) {
                JsonObject moduleData = new JsonObject();
                moduleData.addProperty("enabled", mod.isEnabled());
                JsonObject settingsData = new JsonObject();
                for (Setting setting : mod.getSettings()) {
                    settingsData.add(setting.getName(), setting.serialize());
                }
                moduleData.add("settings", settingsData);
                config.add(mod.getName(), moduleData);
            }
            Files.writeString(file, GSON.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(String name) {
        try {
            if (name == null || name.trim().isEmpty()) return;
            Path file = CONFIG_DIR.resolve(name.trim() + ".json");
            if (!Files.exists(file)) return;
            String json = Files.readString(file);
            JsonElement root = GSON.fromJson(json, JsonElement.class);
            if (root == null || root.isJsonNull() || !root.isJsonObject()) return;
            JsonObject config = root.getAsJsonObject();
            for (Module mod : ModuleManager.getInstance().getModules()) {
                if (!config.has(mod.getName())) continue;
                JsonObject moduleData = config.getAsJsonObject(mod.getName());
                if (moduleData.has("enabled")) mod.setEnabled(moduleData.get("enabled").getAsBoolean());
                if (moduleData.has("settings")) {
                    JsonObject settingsData = moduleData.getAsJsonObject("settings");
                    for (Setting setting : mod.getSettings()) {
                        if (settingsData.has(setting.getName())) {
                            JsonElement value = settingsData.get(setting.getName());
                            if (value != null && !value.isJsonNull()) setting.deserialize(value);
                        }
                    }
                }

                // set keybind to right shift if it does not find one
                if (mod instanceof ClickGuiModule) {
                    KeybindSetting keybindSetting = findKeybindSetting(mod);
                    if (keybindSetting != null && keybindSetting.getKey() == 0) {
                        keybindSetting.setKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static KeybindSetting findKeybindSetting(Module mod) {
        for (Setting setting : mod.getSettings()) {
            if (setting instanceof KeybindSetting) {
                return (KeybindSetting) setting;
            }
        }
        return null;
    }

    public static void saveLatestConfig() {
        saveConfig("latest");
    }

    public static void loadLatestConfig() {
        if (Files.exists(LATEST_FILE)) {
            loadConfig("latest");
        }
    }

    public static void deleteConfig(String name) {
        try {
            if (name == null || name.trim().isEmpty()) return;
            Path file = CONFIG_DIR.resolve(name.trim() + ".json");
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> listConfigs() {
        List<String> configs = new ArrayList<>();
        try {
            if (!Files.exists(CONFIG_DIR)) return configs;
            Files.list(CONFIG_DIR)
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .forEach(configs::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configs;
    }

    public static void renderConfigMenu(int accentColor, int textColor) {
        ImGui.textColored(accentColor, "Configs");
        ImGui.separator();
        float topInputWidth = 220;
        float topButtonWidth = 80;
        float topButtonHeight = 28;
        ImGui.pushItemWidth(topInputWidth);
        ImGui.inputText("##ConfigNameInput", newConfigName);
        ImGui.popItemWidth();
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Button, accentColor);
        if (ImGui.button("+ Save", topButtonWidth, topButtonHeight)) {
            String name = newConfigName.get().trim();
            if (!name.isEmpty()) {
                saveConfig(name);
                newConfigName.set("CoolConfig");
            }
        }
        ImGui.popStyleColor();
        ImGui.separator();
        List<String> configs = listConfigs();
        Collections.sort(configs, Collections.reverseOrder());
        if (configs.isEmpty()) {
            ImGui.textColored(0xFF888888, "no configs found :/");
            return;
        }
        float buttonWidth = 50;
        float buttonHeight = 18;
        float spacing = 4;
        for (String cfg : configs) {
            float totalWidth = buttonWidth * 3 + spacing * 2;
            float availWidth = ImGui.getContentRegionAvail().x;
            ImGui.setCursorPosX(ImGui.getCursorPosX() + availWidth - totalWidth);
            ImGui.pushStyleColor(ImGuiCol.Button, accentColor);
            if (ImGui.button("Load##" + cfg, buttonWidth, buttonHeight)) loadConfig(cfg);
            ImGui.sameLine(0, spacing);
            if (ImGui.button("Save##" + cfg, buttonWidth, buttonHeight)) saveConfig(cfg);
            ImGui.sameLine(0, spacing);
            if (ImGui.button("Del##" + cfg, buttonWidth, buttonHeight)) deleteConfig(cfg);
            ImGui.popStyleColor();
            ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getCursorPosX() - availWidth);
            ImGui.setCursorPosY(ImGui.getCursorPosY() + buttonHeight - ImGui.getTextLineHeight());
            ImGui.text(cfg);
            ImGui.dummy(0, 1);
        }
    }
}