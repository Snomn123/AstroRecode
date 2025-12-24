package dev.recode.astro.module.settings;

import com.google.gson.*;
import dev.recode.astro.api.utils.*;
import dev.recode.astro.module.*;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.*;
import imgui.flag.*;
import imgui.type.*;

public class RangeSliderSetting extends Setting {
    private final boolean isInt;
    private final String format;
    private double min, max, absMin, absMax;
    private int activeHandle = -1;

    public RangeSliderSetting(String name, int min, int max, int absMin, int absMax) {
        super(name);
        this.isInt = true;
        this.format = "%.0f";
        this.min = min;
        this.max = max;
        this.absMin = absMin;
        this.absMax = absMax;
    }

    public RangeSliderSetting(String name, float min, float max, float absMin, float absMax) {
        this(name, min, max, absMin, absMax, "%.2f");
    }

    public RangeSliderSetting(String name, float min, float max, float absMin, float absMax, String format) {
        super(name);
        this.isInt = false;
        this.format = format;
        this.min = min;
        this.max = max;
        this.absMin = absMin;
        this.absMax = absMax;
    }

    public RangeSliderSetting(String name, double min, double max, double absMin, double absMax) {
        this(name, min, max, absMin, absMax, "%.2f");
    }

    public RangeSliderSetting(String name, double min, double max, double absMin, double absMax, String format) {
        super(name);
        this.isInt = false;
        this.format = format;
        this.min = min;
        this.max = max;
        this.absMin = absMin;
        this.absMax = absMax;
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
        ImGui.sameLine(w - inputW * 2 - 5f);
        ImFloat fMin = new ImFloat((float) min);
        if (ImGui.inputFloat("##min_" + getName(), fMin, 0, 0, format, ImGuiInputTextFlags.EnterReturnsTrue)) {
            min = Math.max(absMin, Math.min(max, fMin.get()));
        }
        ImGui.setNextItemWidth(inputW);
        ImGui.sameLine(w - inputW);
        ImFloat fMax = new ImFloat((float) max);
        if (ImGui.inputFloat("##max_" + getName(), fMax, 0, 0, format, ImGuiInputTextFlags.EnterReturnsTrue)) {
            max = Math.max(min, Math.min(absMax, fMax.get()));
        }
        ImGui.popStyleVar();


        float h = 20f, r = 8f;
        ImVec2 pos = ImGui.getCursorScreenPos();
        ImDrawList dl = ImGui.getWindowDrawList();
        double minNorm = (min - absMin) / (absMax - absMin), maxNorm = (max - absMin) / (absMax - absMin);
        float minX = pos.x + (float) (minNorm * w), maxX = pos.x + (float) (maxNorm * w), cy = pos.y + h / 2f;

        dl.addRectFilled(pos.x, cy - 2f, pos.x + w, cy + 2f, ImGui.getColorU32(secondary[0], secondary[1], secondary[2], secondary[3]), 2f);
        dl.addRectFilled(minX, cy - 2f, maxX, cy + 2f, ImGui.getColorU32(primary[0], primary[1], primary[2], primary[3] * 0.5f), 2f);

        ImVec2 mouse = ImGui.getMousePos();
        boolean down = ImGui.isMouseDown(0);

        if (ImGui.isWindowHovered() && mouse.y >= pos.y && mouse.y <= pos.y + h && ImGui.isMouseClicked(0)) {
            activeHandle = Math.abs(mouse.x - minX) < Math.abs(mouse.x - maxX) ? 0 : 1;
        }
        if (!down) activeHandle = -1;

        if (activeHandle != -1 && down) {
            float mx = Math.max(pos.x, Math.min(pos.x + w, mouse.x));
            double val = absMin + ((mx - pos.x) / w) * (absMax - absMin);
            if (isInt) val = Math.round(val);
            if (activeHandle == 0) min = Math.max(absMin, Math.min(max, val));
            else max = Math.max(min, Math.min(absMax, val));
        }

        int col = ImGui.getColorU32(primary[0], primary[1], primary[2], primary[3]);
        int hover = ImGui.getColorU32(primary[0] * 1.2f, primary[1] * 1.2f, primary[2] * 1.2f, primary[3]);

        boolean minHovered = Math.abs(mouse.x - minX) < r && Math.abs(mouse.y - cy) < r;
        boolean maxHovered = Math.abs(mouse.x - maxX) < r && Math.abs(mouse.y - cy) < r;

        dl.addCircleFilled(minX, cy, r, activeHandle == 0 || minHovered ? hover : col);
        dl.addCircleFilled(maxX, cy, r, activeHandle == 1 || maxHovered ? hover : col);

        ImGui.setCursorScreenPos(pos.x, pos.y);
        ImGui.invisibleButton("##range_" + getName(), w, h);

        if ((minHovered || maxHovered || activeHandle != -1) && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.dummy(w, h);
    }

    public int getRandomInt() { return MathHelper.randomInt((int) min, (int) max); }
    public double getRandomDouble() { return MathHelper.randomDouble(min, max); }
    public int getIntMin() { return (int) min; }
    public int getIntMax() { return (int) max; }
    public double getDoubleMin() { return min; }
    public double getDoubleMax() { return max; }

    @Override
    public JsonElement serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("min", min);
        obj.addProperty("max", max);
        return obj;
    }

    @Override
    public void deserialize(JsonElement data) {
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("min")) min = Math.max(absMin, Math.min(absMax, obj.get("min").getAsDouble()));
            if (obj.has("max")) max = Math.max(min, Math.min(absMax, obj.get("max").getAsDouble()));
        }
    }
}
