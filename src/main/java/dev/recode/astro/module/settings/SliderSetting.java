package dev.recode.astro.module.settings;

import com.google.gson.*;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.*;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.*;
import imgui.flag.*;
import imgui.type.*;

public class SliderSetting extends Setting {
    private final boolean isInt;
    private final String format;
    private double value, min, max;
    private boolean isDragging = false;

    public SliderSetting(String name, int value, int min, int max) {
        super(name);
        this.isInt = true;
        this.format = "%.0f";
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public SliderSetting(String name, float value, float min, float max) {
        this(name, value, min, max, "%.2f");
    }

    public SliderSetting(String name, float value, float min, float max, String format) {
        super(name);
        this.isInt = false;
        this.format = format;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public SliderSetting(String name, double value, double min, double max) {
        this(name, value, min, max, "%.2f");
    }

    public SliderSetting(String name, double value, double min, double max, String format) {
        super(name);
        this.isInt = false;
        this.format = format;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public void render() {
        ClickGuiModule gui = (ClickGuiModule) ModuleManager.getInstance().getModuleByClass(ClickGuiModule.class);
        if (gui == null) return;

        float[] primary = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(gui.primaryColor.getValue()));
        float[] secondary = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(gui.secondaryColor.getValue()));
        float w = ImGui.getContentRegionAvailX(), inputW = 60f;

        ImGui.textColored(primary[0], primary[1], primary[2], primary[3], getName());

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 3f);
        ImGui.setNextItemWidth(inputW);
        ImGui.sameLine(w - inputW);
        ImFloat fValue = new ImFloat((float) value);
        if (ImGui.inputFloat("##value_" + getName(), fValue, 0, 0, format, ImGuiInputTextFlags.EnterReturnsTrue)) {
            value = Math.max(min, Math.min(max, fValue.get()));
            if (isInt) value = Math.round(value);
        }
        ImGui.popStyleVar();

        float h = 20f, r = 8f;
        ImVec2 pos = ImGui.getCursorScreenPos();
        ImDrawList dl = ImGui.getWindowDrawList();
        double norm = (value - min) / (max - min);
        float valueX = pos.x + (float) (norm * w), cy = pos.y + h / 2f;

        dl.addRectFilled(pos.x, cy - 2f, pos.x + w, cy + 2f, ImGui.getColorU32(secondary[0], secondary[1], secondary[2], secondary[3]), 2f);
        dl.addRectFilled(pos.x, cy - 2f, valueX, cy + 2f, ImGui.getColorU32(primary[0], primary[1], primary[2], primary[3] * 0.5f), 2f);

        ImVec2 mouse = ImGui.getMousePos();
        boolean down = ImGui.isMouseDown(0);

        if (ImGui.isWindowHovered() && mouse.y >= pos.y && mouse.y <= pos.y + h && ImGui.isMouseClicked(0)) {
            isDragging = true;
        }
        if (!down) isDragging = false;

        if (isDragging && down) {
            float mx = Math.max(pos.x, Math.min(pos.x + w, mouse.x));
            double val = min + ((mx - pos.x) / w) * (max - min);
            if (isInt) val = Math.round(val);
            value = Math.max(min, Math.min(max, val));
        }

        boolean hovered = Math.abs(mouse.x - valueX) < r && Math.abs(mouse.y - cy) < r;
        int col = ImGui.getColorU32(primary[0], primary[1], primary[2], primary[3]);
        int hover = ImGui.getColorU32(primary[0] * 1.2f, primary[1] * 1.2f, primary[2] * 1.2f, primary[3]);
        dl.addCircleFilled(valueX, cy, r, isDragging || hovered ? hover : col);

        ImGui.setCursorScreenPos(pos.x, pos.y);
        ImGui.invisibleButton("##slider_" + getName(), w, h);

        if ((hovered || isDragging) && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.dummy(w, h);
    }

    public int getIntValue() { return (int) value; }
    public float getFloatValue() { return (float) value; }
    public double getDoubleValue() { return value; }

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
            if (obj.has("value")) {
                value = Math.max(min, Math.min(max, obj.get("value").getAsDouble()));
                if (isInt) value = Math.round(value);
            }
        }
    }
}