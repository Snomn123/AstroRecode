package dev.recode.astro.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.recode.astro.AstroRecodeClient;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.KeybindMode;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.Setting;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImInt;

public class KeybindSetting extends Setting {

    private final ImInt key;
    private final ImInt modeIndex;
    private boolean binding = false;

    public KeybindSetting(String name, int defaultKey, KeybindMode defaultMode) {
        super(name);
        this.key = new ImInt(defaultKey);
        this.modeIndex = new ImInt(defaultMode.ordinal());
    }

    @Override
    public void render() {
        ClickGuiModule clickGUI = getClickGUI();
        if (clickGUI == null) return;

        int primaryColor = clickGUI.primaryColor.getValue();
        float[] accent = ColorHelper.intToFloatArray(
                ColorHelper.argbToAbgr(primaryColor)
        );

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 5.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 6.0f, 6.0f);

        ImGui.columns(2, "keybind_cols_" + getName(), false);
        ImGui.setColumnWidth(0, 140);

        ImGui.textColored(accent[0], accent[1], accent[2], accent[3],
                getName() + ":");
        ImGui.nextColumn();

        ImGui.beginGroup();


        ImGui.text(binding
                ? "waiting..."
                : "bind: " + dev.recode.astro.module.settings.KeybindHelper.getKeyName(key.get())
        );
        ImGui.sameLine();


        if (ImGui.button(binding
                ? "Cancel##" + getName()
                : "Rebind##" + getName(), 70, 0)) {

            if (binding) stopBinding();
            else startBinding();
        }

        ImGui.sameLine();


        if (ImGui.button("Remove##" + getName(), 70, 0)) {
            stopBinding();
            setKey(0);
        }


        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.FrameBg, accent[0], accent[1], accent[2], 0.60f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, accent[0], accent[1], accent[2], 0.75f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, accent[0], accent[1], accent[2], 0.90f);

        ImGui.pushStyleColor(ImGuiCol.Header, accent[0], accent[1], accent[2], 0.65f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, accent[0], accent[1], accent[2], 0.85f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, accent[0], accent[1], accent[2], 1.00f);

        if (ImGui.beginCombo("##mode_" + getName(), getMode().name())) {
            for (KeybindMode mode : KeybindMode.values()) {
                boolean selected = mode == getMode();
                if (ImGui.selectable(mode.name(), selected)) {
                    setMode(mode);
                }
                if (selected) ImGui.setItemDefaultFocus();
            }
            ImGui.endCombo();
        }

        ImGui.popStyleColor(6);

        // Tooltip
        if (ImGui.isItemHovered() && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.endGroup();
        ImGui.nextColumn();
        ImGui.columns(1);

        ImGui.popStyleVar(2);
    }

    public void startBinding() {
        binding = true;
        AstroRecodeClient.getInstance().startBinding(this);
    }

    public void stopBinding() {
        binding = false;
        AstroRecodeClient.getInstance().stopBinding();
    }

    public JsonElement serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("key", key.get());
        obj.addProperty("mode", modeIndex.get());
        return obj;
    }

    public void deserialize(JsonElement data) {
        if (!data.isJsonObject()) return;
        JsonObject obj = data.getAsJsonObject();
        if (obj.has("key")) key.set(obj.get("key").getAsInt());
        if (obj.has("mode")) modeIndex.set(obj.get("mode").getAsInt());
    }

    public int getKey() {
        return key.get();
    }

    public void setKey(int newKey) {
        key.set(newKey);
    }

    public KeybindMode getMode() {
        KeybindMode[] values = KeybindMode.values();
        int idx = Math.max(0, Math.min(modeIndex.get(), values.length - 1));
        return values[idx];
    }

    public void setMode(KeybindMode mode) {
        modeIndex.set(mode.ordinal());
    }

    private ClickGuiModule getClickGUI() {
        return ModuleManager.getInstance()
                .getModuleByClass(ClickGuiModule.class);
    }
}
