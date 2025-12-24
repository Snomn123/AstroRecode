package dev.recode.astro.module.modules.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.Setting;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

import java.util.ArrayList;
import java.util.List;

public class MultiButtonSetting extends Setting {
    private final List<String> options;
    private final List<Boolean> enabled;
    private boolean expanded = false;

    public MultiButtonSetting(String name, List<String> options) {
        super(name);
        this.options = options;
        this.enabled = new ArrayList<>(options.size());
        for (int i = 0; i < options.size(); i++) {
            enabled.add(false);
        }
    }

    @Override
    public void render() {
        ClickGuiModule clickGUI = getClickGUI();
        if (clickGUI == null) return;

        int accentColor = clickGUI.primaryColor.getValue();
        float[] color = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(accentColor));

        int accentU32 = ImGui.getColorU32(color[0], color[1], color[2], color[3]);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 5.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 6.0f, 6.0f);

        List<String> enabledOptions = getEnabledOptions();
        String displayText = enabledOptions.isEmpty() ? "none" : String.join(", ", enabledOptions);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, accentU32);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, accentU32);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, accentU32);
        ImGui.pushStyleColor(ImGuiCol.Button, accentU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, accentU32);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, accentU32);
        ImGui.pushStyleColor(ImGuiCol.Text, 1f, 1f, 1f, 1f);

        ImGui.pushStyleColor(ImGuiCol.Header, accentU32);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, accentU32);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, accentU32);

        ImGui.setNextItemWidth(-1.0f);
        if (ImGui.beginCombo("##" + getName(), getName() + ": " + displayText)) {
            for (int i = 0; i < options.size(); i++) {
                boolean selected = enabled.get(i);

                if (ImGui.selectable(options.get(i), selected, 0)) {
                    enabled.set(i, !enabled.get(i));
                }

                if (selected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }

        if (ImGui.isItemHovered() && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.popStyleColor(10);
        ImGui.popStyleVar(2);
    }

    public boolean isEnabled(String option) {
        int index = options.indexOf(option);
        return index >= 0 && enabled.get(index);
    }

    public void setEnabled(String option, boolean value) {
        int index = options.indexOf(option);
        if (index >= 0) {
            enabled.set(index, value);
        }
    }

    public List<String> getEnabledOptions() {
        List<String> active = new ArrayList<>();
        for (int i = 0; i < options.size(); i++)
            if (enabled.get(i)) active.add(options.get(i));
        return active;
    }

    @Override
    public JsonElement serialize() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for (int i = 0; i < options.size(); i++) {
            if (enabled.get(i)) {
                arr.add(options.get(i));
            }
        }
        obj.add("enabled", arr);
        return obj;
    }

    @Override
    public void deserialize(JsonElement data) {
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("enabled") && obj.get("enabled").isJsonArray()) {
                JsonArray arr = obj.getAsJsonArray("enabled");
                for (int i = 0; i < enabled.size(); i++) {
                    enabled.set(i, false);
                }
                for (JsonElement elem : arr) {
                    String option = elem.getAsString();
                    setEnabled(option, true);
                }
            }
        }
    }

    private ClickGuiModule getClickGUI() {
        return ModuleManager.getInstance().getModuleByClass(ClickGuiModule.class);
    }
}