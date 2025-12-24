package dev.recode.astro.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.Setting;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;

public class StringSetting extends Setting {
    private final ImString value;
    private final int maxLength;

    public StringSetting(String name, String defaultValue) {
        super(name);
        this.maxLength = Math.max(128, defaultValue.length() + 32);
        this.value = new ImString(defaultValue, maxLength);
    }

    public StringSetting(String name, String defaultValue, int maxLength) {
        super(name);
        this.maxLength = maxLength;
        this.value = new ImString(defaultValue, maxLength);
    }

    @Override
    public void render() {
        ClickGuiModule clickGUI = getClickGUI();
        if (clickGUI == null) return;

        int primaryColor = clickGUI.primaryColor.getValue();
        float[] accent = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(primaryColor));
        int uniformColor = ImGui.getColorU32(accent[0], accent[1], accent[2], accent[3]);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 5.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 6.0f, 6.0f);

        ImGui.textColored(accent[0], accent[1], accent[2], accent[3], getName() + ":");

        ImGui.pushStyleColor(ImGuiCol.FrameBg, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.Text, 1f, 1f, 1f, 1f);

        ImGui.setNextItemWidth(-1.0f);
        ImGui.inputTextWithHint("##" + getName(), getName() + ": ", value);

        if (ImGui.isItemHovered() && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.popStyleColor(4);
        ImGui.popStyleVar(2);
    }

    public JsonElement serialize() {
        JsonObject data = new JsonObject();
        data.addProperty("value", value.get());
        data.addProperty("maxLength", maxLength);
        return data;
    }

    public void deserialize(JsonElement data) {
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("value")) {
                value.set(obj.get("value").getAsString());
            }
        }
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    private ClickGuiModule getClickGUI() {
        return (ClickGuiModule) ModuleManager.getInstance().getModuleByClass(ClickGuiModule.class);
    }
}