package dev.recode.astro.screens.menu;

import dev.recode.astro.api.imgui.ImGuiImpl;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.module.Category;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

public final class TopBarRenderer {

    private static final float TOPBAR_H = 35f;
    private static final float CAT_BTN_W = 80f;
    private static final float CAT_BTN_H = 22f;
    private static final float ICON_SIZE = 20f;
    private static final float ICON_SPACING = 8f;
    private static final float ICON_MARGIN_RIGHT = 12f;

    private TopBarRenderer() {}

    public static void render(ClickGuiState state, int accent, int secondary, int bg) {
        loadIcons(state);

        ImGui.beginChild("TopBar", 0, TOPBAR_H, false, ImGuiWindowFlags.NoScrollbar);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10, 0);

        renderCategoryButtons(state, accent, secondary, bg);
        renderIconButtons(state, accent, secondary);

        ImGui.popStyleVar();
        ImGui.endChild();
    }

    private static void loadIcons(ClickGuiState state) {
        if (state.configIcon == -1) state.configIcon = ImGuiImpl.getTextureID("/assets/folder.png");
        if (state.friendsIcon == -1) state.friendsIcon = ImGuiImpl.getTextureID("/assets/friends.png");
    }

    private static void renderCategoryButtons(ClickGuiState state, int accent, int secondary, int bg) {
        float[] bgF = ColorHelper.intToFloatArray(bg);
        float[] whiteF = {1f, 1f, 1f, 1f};
        int transparent = ColorHelper.floatArrayToInt(new float[]{bgF[0], bgF[1], bgF[2], 0f});
        int white = ColorHelper.floatArrayToInt(whiteF);
        int dimText = blendColors(bg, white, 0.73f);

        for (Category cat : Category.values()) {
            boolean selected = state.selectedCategory == cat && !state.configTabActive && !state.friendsTabActive;
            int btnColor = selected ? accent : transparent;
            int textColor = selected ? white : dimText;

            ImGui.pushStyleColor(ImGuiCol.Button, btnColor);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, btnColor);
            ImGui.pushStyleColor(ImGuiCol.Text, textColor);

            if (ImGui.button(cat.name(), CAT_BTN_W, CAT_BTN_H)) {
                state.selectedCategory = cat;
                state.configTabActive = false;
                state.friendsTabActive = false;
            }

            ImGui.popStyleColor(3);
            ImGui.sameLine();
        }
    }

    private static void renderIconButtons(ClickGuiState state, int accent, int secondary) {
        float totalIconsWidth = (ICON_SIZE * 2) + ICON_SPACING;
        float iconsX = ImGui.getWindowWidth() - totalIconsWidth - ICON_MARGIN_RIGHT;
        float iconY = (TOPBAR_H - ICON_SIZE) / 2f;

        ImGui.setCursorPosX(iconsX);
        ImGui.setCursorPosY(iconY);
        renderIconButton((long) state.configIcon, state.configTabActive, accent, secondary, "config panel", () -> {
            state.configTabActive = !state.configTabActive;
            state.friendsTabActive = false;
        });

        ImGui.setCursorPosX(iconsX + ICON_SIZE + ICON_SPACING);
        ImGui.setCursorPosY(iconY);
        renderIconButton((long) state.friendsIcon, state.friendsTabActive, accent, secondary, "homie panel", () -> {
            state.friendsTabActive = !state.friendsTabActive;
            state.configTabActive = false;
        });
    }

    private static void renderIconButton(long textureId, boolean active, int accent, int secondary, String tooltip, Runnable onClick) {
        float[] secF = ColorHelper.intToFloatArray(secondary);
        int dimmed = ColorHelper.floatArrayToInt(new float[]{secF[0], secF[1], secF[2], 160f / 255f});
        int tint = active ? accent : dimmed;

        ImGui.pushStyleColor(ImGuiCol.Button, tint);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, tint);

        ImGui.image(textureId, ICON_SIZE, ICON_SIZE);

        if (ImGui.isItemHovered()) ImGui.setTooltip(tooltip);
        if (ImGui.isItemClicked()) onClick.run();

        ImGui.popStyleColor(2);
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