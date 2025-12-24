package dev.recode.astro.module.settings;

import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.Setting;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.ImGui;

public class SeparatorSetting extends Setting {
    private final String text;

    public SeparatorSetting() {
        super("");
        this.text = null;
    }

    public SeparatorSetting(String text) {
        super(text);
        this.text = text;
    }

    @Override
    public void render() {
        ClickGuiModule clickGUI = getClickGUI();
        if (clickGUI == null) return;

        int primary = clickGUI.primaryColor.getValue();
        float[] col = ColorHelper.intToFloatArray(ColorHelper.argbToAbgr(primary));

        ImGui.spacing();

        if (text == null || text.isEmpty()) {
            ImGui.separator();
        } else {
            float width = ImGui.getContentRegionAvailX();
            float textWidth = ImGui.calcTextSize(text).x;
            float padding = 8f;

            float cursorX = ImGui.getCursorScreenPosX();
            float cursorY = ImGui.getCursorScreenPosY();
            float lineY = cursorY + ImGui.getTextLineHeight() * 0.5f;

            float leftLineEnd = cursorX + (width - textWidth) * 0.5f - padding;
            float textStart = cursorX + (width - textWidth) * 0.5f;
            float rightLineStart = cursorX + (width + textWidth) * 0.5f + padding;
            float rightLineEnd = cursorX + width;

            int lineColor = ImGui.getColorU32(col[0], col[1], col[2], col[3]);

            ImGui.getWindowDrawList().addLine(cursorX, lineY, leftLineEnd, lineY, lineColor);
            ImGui.setCursorPosX(textStart - cursorX + ImGui.getCursorPosX());
            ImGui.textColored(col[0], col[1], col[2], col[3], text);
            ImGui.getWindowDrawList().addLine(rightLineStart, lineY, rightLineEnd, lineY, lineColor);
        }

        ImGui.spacing();
    }

    private ClickGuiModule getClickGUI() {
        return (ClickGuiModule) ModuleManager.getInstance().getModuleByClass(ClickGuiModule.class);
    }
}