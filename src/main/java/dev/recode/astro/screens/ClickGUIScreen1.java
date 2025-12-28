package dev.recode.astro.screens;

import dev.recode.astro.api.imgui.ImGuiImpl;
import dev.recode.astro.api.imgui.RenderInterface;
import dev.recode.astro.api.config.ConfigCFG;
import dev.recode.astro.api.config.FriendCFG;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.modules.client.ClickGuiModule;
import dev.recode.astro.module.Setting;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;

public final class ClickGUIScreen1 extends Screen implements RenderInterface {
    private Category selectedCategory = Category.values()[0];
    private boolean configTabActive = false;
    private boolean friendsTabActive = false;
    private final Set<Module> expandedModules = new HashSet<>();
    private int configIcon = -1;
    private int friendsIcon = -1;

    public ClickGUIScreen1() {
        super(Component.literal("CG1"));
    }

    @Override
    public void render(ImGuiIO io) {
        ClickGuiModule gui = (ClickGuiModule) ModuleManager.getInstance().getModuleByName("ClickGUI");
        int accent = gui != null ? gui.primaryColor.getValue() : 0xFF6969FF;
        int secondary = gui != null ? gui.secondaryColor.getValue() : 0xFF5050;
        int bg = gui != null ? gui.backgroundColor.getValue() : 0xFF202020;

        applyTheme(accent, secondary, bg);

        ImGui.setNextWindowSize(900, 550, 0);
        ImGui.setNextWindowPos((ImGui.getIO().getDisplaySizeX() - 900) / 2f,
                (ImGui.getIO().getDisplaySizeY() - 550) / 2f, 0);
        ImGui.begin("Astro (Recode)", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);

        if (configIcon == -1) configIcon = ImGuiImpl.getTextureID("/assets/folder.png");
        if (friendsIcon == -1) friendsIcon = ImGuiImpl.getTextureID("/assets/friends.png");

        renderTopBar(accent, secondary);

        ImGui.beginChild("ContentArea", 0, 0, true);
        if (ImGuiImpl.getBoldFont() != null) ImGui.pushFont(ImGuiImpl.getBoldFont());

        if (configTabActive) {
            ConfigCFG.renderConfigMenu(accent, 0xFFFFFFFF);
        } else if (friendsTabActive) {
            FriendCFG.renderFriends(accent, secondary);
        } else {
            for (Module mod : ModuleManager.getInstance().getModulesByCategory(selectedCategory)) {
                renderModule(mod, accent, secondary, bg);
            }
        }

        if (ImGuiImpl.getBoldFont() != null) ImGui.popFont();
        ImGui.endChild();
        ImGui.end();

        ImGui.popStyleColor(11);
        ImGui.popStyleVar(5);
    }

    private void renderTopBar(int accent, int secondary) {
        if (ImGuiImpl.getBoldFont() != null) ImGui.pushFont(ImGuiImpl.getBoldFont());

        ImGui.beginChild("TopBar", 0, 35, false, ImGuiWindowFlags.NoScrollbar);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10, 0);

        float barWidth = ImGui.getContentRegionAvailX();
        float buttonSize = 20f;
        float catWidth = 80f;

        for (Category cat : Category.values()) {
            boolean sel = selectedCategory == cat && !configTabActive && !friendsTabActive;
            ImGui.pushStyleColor(ImGuiCol.Button, sel ? accent : 0x00000000);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, sel ? accent : 0x00000000);
            ImGui.pushStyleColor(ImGuiCol.Text, sel ? 0xFFFFFFFF : 0xFFBBBBBB);

            if (ImGui.button(cat.name(), catWidth, 22)) {
                selectedCategory = cat;
                configTabActive = false;
                friendsTabActive = false;
            }

            ImGui.popStyleColor(3);
            ImGui.sameLine();
        }

        float iconsStartX = barWidth - 60;
        ImGui.setCursorPosX(iconsStartX);

        ImGui.pushStyleColor(ImGuiCol.Button, configTabActive ? accent : alpha(secondary, 160));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, configTabActive ? accent : alpha(secondary, 160));
        ImGui.image((long) configIcon, buttonSize, buttonSize);
        boolean configHovered = ImGui.isItemHovered();
        boolean configClicked = ImGui.isItemClicked();
        if (configClicked) {
            configTabActive = !configTabActive;
            friendsTabActive = false;
            if (configTabActive) selectedCategory = Category.values()[0];
        }
        if (configHovered) ImGui.setTooltip("config panel");
        ImGui.sameLine();
        ImGui.popStyleColor(2);

        ImGui.pushStyleColor(ImGuiCol.Button, friendsTabActive ? accent : alpha(secondary, 160));
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, friendsTabActive ? accent : alpha(secondary, 160));
        ImGui.image((long) friendsIcon, buttonSize, buttonSize);
        boolean friendsHovered = ImGui.isItemHovered();
        boolean friendsClicked = ImGui.isItemClicked();
        if (friendsClicked) {
            friendsTabActive = !friendsTabActive;
            configTabActive = false;
            if (friendsTabActive) selectedCategory = Category.values()[0];
        }
        if (friendsHovered) ImGui.setTooltip("homie panel");
        ImGui.popStyleColor(2);

        ImGui.popStyleVar();
        ImGui.endChild();
        if (ImGuiImpl.getBoldFont() != null) ImGui.popFont();
    }

    private void renderModule(Module mod, int accent, int secondary, int bg) {
        boolean enabled = mod.isEnabled();
        boolean expanded = expandedModules.contains(mod);
        boolean hasSettings = !mod.getSettings().isEmpty();

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 4.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 8);

        int btnBg = enabled ? accent : secondary;

        ImGui.pushStyleColor(ImGuiCol.Button, btnBg);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, btnBg);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, btnBg);
        ImGui.pushStyleColor(ImGuiCol.Text, enabled ? 0xFFFFFFFF : 0xFFCCCCCC);

        float width = ImGui.getContentRegionAvailX();

        if (ImGui.button(mod.getName(), width, 40)) mod.setEnabled(!enabled);

        if (ImGui.isItemHovered() && !mod.getDescription().isEmpty()) {
            ImGui.setTooltip(mod.getDescription());
        }

        ImGui.popStyleColor(4);
        ImGui.popStyleVar(2);

        if (ImGui.isItemClicked(1) && hasSettings) {
            if (expanded) expandedModules.remove(mod);
            else expandedModules.add(mod);
        }

        if (expanded && hasSettings) {
            ImGui.indent(20);

            int bgAlpha = (bg >> 24) & 0xFF;
            ImGui.pushStyleColor(ImGuiCol.FrameBg, alpha(bg, Math.min(210, bgAlpha)));
            ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, alpha(bg, Math.min(210, bgAlpha)));
            ImGui.pushStyleColor(ImGuiCol.FrameBgActive, alpha(bg, Math.min(210, bgAlpha)));
            ImGui.pushStyleColor(ImGuiCol.SliderGrab, accent);
            ImGui.pushStyleColor(ImGuiCol.SliderGrabActive, accent);
            ImGui.pushStyleColor(ImGuiCol.CheckMark, accent);
            ImGui.pushStyleColor(ImGuiCol.Text, 0xFFEEEEEE);
            ImGui.pushItemWidth(300);

            for (Setting s : mod.getSettings()) {
                s.render();
                ImGui.dummy(0, 1);
            }

            ImGui.popItemWidth();
            ImGui.popStyleColor(7);
            ImGui.unindent(20);
        }

        ImGui.dummy(0, 3);
    }

    private void applyTheme(int accent, int secondary, int bg) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 8.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ChildRounding, 6.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 4.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ScrollbarRounding, 8.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.GrabRounding, 4.0f);

        int bgAlpha = (bg >> 24) & 0xFF;

        ImGui.pushStyleColor(ImGuiCol.WindowBg, alpha(bg, Math.min(235, bgAlpha)));
        ImGui.pushStyleColor(ImGuiCol.ChildBg, alpha(bg, Math.min(220, bgAlpha)));
        ImGui.pushStyleColor(ImGuiCol.Text, 0xFFFFFFFF);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, alpha(bg, Math.min(190, bgAlpha)));
        ImGui.pushStyleColor(ImGuiCol.TitleBg, bg);
        ImGui.pushStyleColor(ImGuiCol.TitleBgActive, bg);
        ImGui.pushStyleColor(ImGuiCol.Button, accent);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, secondary);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, accent);
        ImGui.pushStyleColor(ImGuiCol.Border, secondary);
        ImGui.pushStyleColor(ImGuiCol.PopupBg, accent);
    }

    private int alpha(int color, int a) {
        return (color & 0x00FFFFFF) | ((a & 0xFF) << 24);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
