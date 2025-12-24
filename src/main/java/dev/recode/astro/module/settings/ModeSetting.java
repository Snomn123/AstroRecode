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

import java.util.List;

public class ModeSetting extends Setting {
    private int index;
    private final List<String> modes;

    public ModeSetting(String name, List<String> modes, int defaultIndex) {
        super(name);
        this.modes = modes;
        this.index = Math.min(defaultIndex, modes.size() - 1);
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

        ImGui.pushStyleColor(ImGuiCol.FrameBg, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.Button, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.Text, 1f, 1f, 1f, 1f);
        ImGui.pushStyleColor(ImGuiCol.Header, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, uniformColor);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, uniformColor);

        ImGui.setNextItemWidth(-1.0f);
        if (ImGui.beginCombo("##" + getName(), getName() + ": " + getMode())) {
            for (int i = 0; i < modes.size(); i++) {
                boolean selected = (index == i);
                if (ImGui.selectable(modes.get(i), selected))
                    index = i;
                if (selected)
                    ImGui.setItemDefaultFocus();
            }
            ImGui.endCombo();
        }

        if (ImGui.isItemHovered() && hasDescription()) {
            ImGui.setTooltip(getDescription());
        }

        ImGui.popStyleColor(10);
        ImGui.popStyleVar(2);
    }

    public JsonElement serialize() {
        JsonObject data = new JsonObject();
        data.addProperty("index", index);
        data.addProperty("mode", getMode());
        return data;
    }

    public void deserialize(JsonElement data) {
        if (data.isJsonObject()) {
            JsonObject obj = data.getAsJsonObject();
            if (obj.has("index")) {
                index = obj.get("index").getAsInt();
            } else if (obj.has("mode")) {
                setMode(obj.get("mode").getAsString());
            }
        }
    }

    public String getMode() {
        return modes.get(index);
    }

    public void setMode(String mode) {
        for (int i = 0; i < modes.size(); i++) {
            if (modes.get(i).equalsIgnoreCase(mode)) {
                index = i;
                return;
            }
        }
    }



    private ClickGuiModule getClickGUI() {
        return (ClickGuiModule) ModuleManager.getInstance().getModuleByClass(ClickGuiModule.class);
    }
}