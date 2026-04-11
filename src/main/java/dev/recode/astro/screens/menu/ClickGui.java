package dev.recode.astro.screens.menu;

import dev.recode.astro.AstroRecode;
import dev.recode.astro.api.imgui.ImGuiImpl;
import dev.recode.astro.api.imgui.RenderInterface;
import dev.recode.astro.api.utils.ColorHelper;
import dev.recode.astro.screens.menu.other.ConfigCFG;
import dev.recode.astro.screens.menu.other.FriendCFG;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClickGui extends Screen implements RenderInterface {

    private static final float WINDOW_W = 900f;
    private static final float WINDOW_H = 550f;

    private final ClickGuiState state = new ClickGuiState();

    public ClickGui() {
        super(Component.literal("CG1"));
    }

    @Override
    public void render(ImGuiIO io) {
        ClickGuiModule gui = (ClickGuiModule) ModuleManager.getInstance().getModuleByName("ClickGUI");
        if (gui == null) return;

        int accent = gui.primaryColor.getValue();
        int secondary = gui.secondaryColor.getValue();
        int bg = gui.backgroundColor.getValue();

        applyTheme(accent, secondary, bg);

        float cx = (io.getDisplaySizeX() - WINDOW_W) / 2f;
        float cy = (io.getDisplaySizeY() - WINDOW_H) / 2f;
        ImGui.setNextWindowSize(WINDOW_W, WINDOW_H, 0);
        ImGui.setNextWindowPos(cx, cy, 4);

        ImGui.begin(AstroRecode.NAME, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse);

        pushFont();
        TopBarRenderer.render(state, accent, secondary, bg);
        renderContent(accent, secondary, bg);
        popFont();

        ImGui.end();

        ImGui.popStyleColor(state.themeColorPushes);
        ImGui.popStyleVar(state.themeVarPushes);
        state.themeColorPushes = 0;
        state.themeVarPushes = 0;
    }

    private void renderContent(int accent, int secondary, int bg) {
        ImGui.beginChild("ContentArea", 0, 0, true);

        if (state.configTabActive) {
            ConfigCFG.renderConfigMenu(accent, white());
        } else if (state.friendsTabActive) {
            FriendCFG.renderFriends(accent, secondary);
        } else {
            for (Module mod : ModuleManager.getInstance().getModulesByCategory(state.selectedCategory)) {
                ModuleRenderer.renderModule(state, mod, accent, secondary, bg);
            }
        }

        ImGui.endChild();
    }

    private void applyTheme(int accent, int secondary, int bg) {
        state.themeVarPushes = 0;
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 8.0f); state.themeVarPushes++;
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 6.0f); state.themeVarPushes++;
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 4.0f); state.themeVarPushes++;
        ImGui.pushStyleVar(ImGuiStyleVar.ScrollbarRounding, 8.0f); state.themeVarPushes++;
        ImGui.pushStyleVar(ImGuiStyleVar.GrabRounding, 4.0f); state.themeVarPushes++;

        int bgSolid = alpha(bg, Math.min(235, alpha255(bg)));
        int bgChild = alpha(bg, Math.min(220, alpha255(bg)));
        int bgFrame = alpha(bg, Math.min(190, alpha255(bg)));

        state.themeColorPushes = 0;
        ImGui.pushStyleColor(ImGuiCol.WindowBg, bgSolid); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.ChildBg, bgChild); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.Text, white()); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.FrameBg, bgFrame); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.TitleBg, bg); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.TitleBgActive, bg); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.Button, accent); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, secondary); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, accent); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.Border, secondary); state.themeColorPushes++;
        ImGui.pushStyleColor(ImGuiCol.PopupBg, accent); state.themeColorPushes++;
    }

    private int alpha(int color, int a) {
        return (color & 0x00FFFFFF) | ((a & 0xFF) << 24);
    }

    private int alpha255(int color) {
        return (color >> 24) & 0xFF;
    }

    private int white() {
        return ColorHelper.floatArrayToInt(new float[]{1f, 1f, 1f, 1f});
    }

    private void pushFont() {
        if (ImGuiImpl.getBoldFont() != null) ImGui.pushFont(ImGuiImpl.getBoldFont());
    }

    private void popFont() {
        if (ImGuiImpl.getBoldFont() != null) ImGui.popFont();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}