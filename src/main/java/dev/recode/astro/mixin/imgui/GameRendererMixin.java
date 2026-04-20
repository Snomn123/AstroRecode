package dev.recode.astro.mixin.imgui;

import dev.recode.astro.api.event.events.ClientRenderEvent;
import dev.recode.astro.api.imgui.ImGuiImpl;
import dev.recode.astro.api.imgui.RenderInterface;
import dev.recode.astro.api.utils.OrbitManager;
import imgui.ImGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        if (minecraft.screen instanceof final RenderInterface renderInterface) {
            ImGuiImpl.beginImGuiRendering();
            renderInterface.render(ImGui.getIO());
            ImGuiImpl.endImGuiRendering();
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void postClientRenderEvent(DeltaTracker deltaTracker, boolean tick, CallbackInfo ci) {
        ClientRenderEvent event = new ClientRenderEvent(deltaTracker, tick);
        OrbitManager.getEventBus().post(event);
    }

}
