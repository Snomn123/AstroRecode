package dev.recode.astro.module.settings;

import com.google.gson.*;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.*;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.*;

public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, boolean value) {
        super(name);
        this.value = value;
    }

    @Override
    public void render() {
        ClickGuiModule gui = (ClickGuiModule) ModuleManager.getInstance().getModuleByClass(ClickGuiModule.class);
        if (gui == null) return;

        float[] primary = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(gui.primaryColor.getValue()));
        float[] secondary = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(gui.secondaryColor.getValue()));

        float boxSize = 16f;
        float spacing = 8f;
        ImVec2 pos = ImGui.getCursorScreenPos();
        ImDrawList dl = ImGui.getWindowDrawList();

        if (ImGui.invisibleButton("##check_" + getName(), boxSize, boxSize)) {
            value = !value;
        }

        int borderCol = ImGui.getColorU32(primary[0], primary[1], primary[2], primary[3]);
        dl.addRect(pos.x, pos.y, pos.x + boxSize, pos.y + boxSize, borderCol, 2f, 0, 1.5f);

        if (value) {
            dl.addRectFilled(pos.x + 3f, pos.y + 3f, pos.x + boxSize - 3f, pos.y + boxSize - 3f,
                    ImGui.getColorU32(primary[0], primary[1], primary[2], primary[3]), 1f);
        }

        if (ImGui.isItemHovered() && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.sameLine(0, spacing);
        float textY = pos.y + boxSize - ImGui.getTextLineHeight();
        ImGui.setCursorScreenPos(ImGui.getCursorScreenPosX(), textY);
        ImGui.textColored(primary[0], primary[1], primary[2], primary[3], getName());

        ImGui.dummy(0, boxSize);
    }

    public boolean getValue() { return value; }
    public void setValue(boolean value) { this.value = value; }

    @Override
    public JsonElement serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("value", value);
        return obj;
    }

    @Override
    public void deserialize(JsonElement data) {
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("value")) value = obj.get("value").getAsBoolean();
        }
    }
}