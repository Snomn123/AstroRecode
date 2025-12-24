package dev.recode.astro.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.Setting;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;

public class ColorSetting extends Setting {
    private int value;
    private int moduleColor;

    public ColorSetting(String name, int defaultValue, int moduleColor) {
        super(name);
        this.value = defaultValue;
        this.moduleColor = moduleColor;
    }

    @Override
    public void render() {
        float[] color = ColorHelper.intToFloatArray(value);

        float outlineR = 1.0f - color[0];
        float outlineG = 1.0f - color[1];
        float outlineB = 1.0f - color[2];

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4.0f, 4.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 4.0f);

        float buttonWidth = 150.0f;
        float buttonHeight = 18.0f;

        float startX = ImGui.getCursorScreenPosX();
        float startY = ImGui.getCursorScreenPosY();

        var drawList = ImGui.getWindowDrawList();
        drawList.addRect(
                startX - 1, startY - 1,
                startX + buttonWidth + 1, startY + buttonHeight + 1,
                ImGui.getColorU32(outlineR, outlineG, outlineB, 1.0f),
                3.0f, 0, 1.5f
        );

        if (ImGui.colorButton("##" + getName(), color[0], color[1], color[2], color[3],
                ImGuiColorEditFlags.NoTooltip, buttonWidth, buttonHeight)) {
            ImGui.openPopup("picker_" + getName());
        }

        if (ImGui.isItemHovered() && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.sameLine();
        ImGui.textColored(outlineR, outlineG, outlineB, 1.0f, getName());

        if (ImGui.beginPopup("picker_" + getName())) {
            ImGui.setNextWindowSize(280, 300, ImGuiCond.FirstUseEver);
            if (ImGui.colorPicker4("##picker", color,
                    ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.DisplayRGB)) {
                value = ColorHelper.floatArrayToInt(color);
            }
            ImGui.endPopup();
        }

        ImGui.popStyleVar(3);
    }

    public JsonElement serialize() {
        JsonObject data = new JsonObject();
        data.addProperty("value", value);
        data.addProperty("moduleColor", moduleColor);
        return data;
    }

    public void deserialize(JsonElement data) {
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("value")) value = obj.get("value").getAsInt();
            if (obj.has("moduleColor")) moduleColor = obj.get("moduleColor").getAsInt();
        }
    }

    public int getValue() {
        return ColorHelper.argbToAbgr(value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setModuleColor(int moduleColor) {
        this.moduleColor = moduleColor;
    }
}