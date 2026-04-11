package dev.recode.astro.screens.menu;

import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.Setting;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

public final class ModuleRenderer {

    private static final float MODULE_H = 40f;

    private ModuleRenderer() {}

    public static void renderModule(ClickGuiState state, Module mod, int accent, int secondary, int bg) {
        boolean enabled = mod.isEnabled();
        boolean expanded = state.expandedModules.contains(mod);
        boolean hasSettings = !mod.getSettings().isEmpty();

        int white = white();
        int mutedText = blendColors(bg, white, 0.80f);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 4.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 8);

        int btnColor = enabled ? accent : secondary;
        int textColor = enabled ? white : mutedText;

        ImGui.pushStyleColor(ImGuiCol.Button, btnColor);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, btnColor);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, btnColor);
        ImGui.pushStyleColor(ImGuiCol.Text, textColor);

        if (ImGui.button(mod.getName(), ImGui.getContentRegionAvailX(), MODULE_H)) {
            mod.setEnabled(!enabled);
        }

        if (ImGui.isItemHovered() && !mod.getDescription().isEmpty()) {
            ImGui.setTooltip(mod.getDescription());
        }

        ImGui.popStyleColor(4);
        ImGui.popStyleVar(2);

        if (ImGui.isItemClicked(1) && hasSettings) {
            if (expanded) state.expandedModules.remove(mod);
            else state.expandedModules.add(mod);
        }

        if (expanded && hasSettings) {
            renderSettings(mod, accent, bg);
        }

        ImGui.dummy(0, 3);
    }

    private static void renderSettings(Module mod, int accent, int bg) {
        int white = white();
        int settingText = blendColors(bg, white, 0.93f);
        int clampedBg = alpha(bg, Math.min(210, alpha255(bg)));

        ImGui.indent(20);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, clampedBg);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, clampedBg);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, clampedBg);
        ImGui.pushStyleColor(ImGuiCol.SliderGrab, accent);
        ImGui.pushStyleColor(ImGuiCol.SliderGrabActive, accent);
        ImGui.pushStyleColor(ImGuiCol.CheckMark, accent);
        ImGui.pushStyleColor(ImGuiCol.Text, settingText);
        ImGui.pushItemWidth(300);

        for (Setting s : mod.getSettings()) {
            s.render();
            ImGui.dummy(0, 1);
        }

        ImGui.popItemWidth();
        ImGui.popStyleColor(7);
        ImGui.unindent(20);
    }

    private static int alpha(int color, int a) {
        return (color & 0x00FFFFFF) | ((a & 0xFF) << 24);
    }

    private static int alpha255(int color) {
        return (color >> 24) & 0xFF;
    }

    private static int white() {
        return ColorHelper.floatArrayToInt(new float[]{1f, 1f, 1f, 1f});
    }

    private static int blendColors(int base, int target, float t) {
        float[] b = ColorHelper.intToFloatArray(base);
        float[] tgt = ColorHelper.intToFloatArray(target);
        return ColorHelper.floatArrayToInt(new float[]{
            b[0] + (tgt[0] - b[0]) * t,
            b[1] + (tgt[1] - b[1]) * t,
            b[2] + (tgt[2] - b[2]) * t,
            Math.max(b[3], tgt[3])
        });
    }
}