package dev.recode.astro.api.imgui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlTexture;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.stb.STBImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ImGuiImpl {
    private static final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static short[] glyphRanges;
    private static ImFont regularFont;
    private static ImFont boldFont;
    private static ImFont titleFont;
    private static final Map<String, Integer> TEXTURE_CACHE = new HashMap<>();
    private static final List<RenderInterface> overlays = new ArrayList<>();

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();
        ImGuiIO data = ImGui.getIO();
        data.setIniFilename("modid.ini");
        data.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        try {
            regularFont = loadFont("/fonts/Rubik-VariableFont_wght.ttf", 16);
            boldFont = loadFont("/fonts/Rubik-VariableFont_wght.ttf", 18);
            titleFont = loadFont("/fonts/Rubik-VariableFont_wght.ttf", 22);
            data.getFonts().build();
        } catch (Exception e) {
            regularFont = null;
            boldFont = null;
            titleFont = null;
        }
        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
    }

    public static void beginImGuiRendering() {
        RenderTarget framebuffer = Minecraft.getInstance().getMainRenderTarget();
        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, ((GlTexture) framebuffer.getColorTexture()).getFbo(((GlDevice) RenderSystem.getDevice()).directStateAccess(), null));
        GL11C.glViewport(0, 0, framebuffer.width, framebuffer.height);
        imGuiImplGl3.newFrame();
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public static void endImGuiRendering() {
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    public static void registerOverlay(RenderInterface overlay) {
        if (!overlays.contains(overlay)) {
            overlays.add(overlay);
        }
    }

    public static void unregisterOverlay(RenderInterface overlay) {
        overlays.remove(overlay);
    }

    public static void renderOverlays() {
        if (overlays.isEmpty()) return;

        beginImGuiRendering();
        for (RenderInterface overlay : overlays) {
            overlay.render(ImGui.getIO());
        }
        endImGuiRendering();
    }

    public static ImFont getRegularFont() {
        return regularFont;
    }

    public static ImFont getBoldFont() {
        return boldFont;
    }

    public static ImFont getTitleFont() {
        return titleFont;
    }

    private static ImFont loadFont(final String path, final int pixelSize) {
        if (glyphRanges == null) {
            ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
            rangesBuilder.addRanges(ImGui.getIO().getFonts().getGlyphRangesDefault());
            rangesBuilder.addRanges(ImGui.getIO().getFonts().getGlyphRangesCyrillic());
            rangesBuilder.addRanges(ImGui.getIO().getFonts().getGlyphRangesJapanese());
            glyphRanges = rangesBuilder.buildRanges();
        }
        ImFontConfig config = new ImFontConfig();
        config.setGlyphRanges(glyphRanges);
        try (InputStream in = Objects.requireNonNull(ImGuiImpl.class.getResourceAsStream(path))) {
            byte[] fontData = IOUtils.toByteArray(in);
            return ImGui.getIO().getFonts().addFontFromMemoryTTF(fontData, pixelSize, config);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load font from path: " + path, e);
        } finally {
            config.destroy();
        }
    }

    public static int getTextureID(String path) {
        if (TEXTURE_CACHE.containsKey(path)) return TEXTURE_CACHE.get(path);
        try (InputStream in = ImGuiImpl.class.getResourceAsStream(path)) {
            if (in == null) throw new IOException("Texture not found: " + path);
            byte[] bytes = IOUtils.toByteArray(in);
            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(bytes.length);
            imageBuffer.put(bytes).flip();
            IntBuffer x = BufferUtils.createIntBuffer(1);
            IntBuffer y = BufferUtils.createIntBuffer(1);
            IntBuffer comp = BufferUtils.createIntBuffer(1);
            STBImage.stbi_set_flip_vertically_on_load(false);
            ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, x, y, comp, 4);
            if (image == null) throw new IOException("Failed to load image: " + STBImage.stbi_failure_reason());
            int texId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, x.get(0), y.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);
            TEXTURE_CACHE.put(path, texId);
            return texId;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load texture: " + path, e);
        }
    }

    public static void dispose() {
        imGuiImplGl3.shutdown();
        imGuiImplGlfw.shutdown();
        ImPlot.destroyContext();
        ImGui.destroyContext();
    }
}