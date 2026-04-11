package dev.recode.astro.mixin;

import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.modules.misc.ClientSpoofModule;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {

    @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onGetClientModName(CallbackInfoReturnable<String> cir) {
        ClientSpoofModule module = ModuleManager.getInstance().getModuleByClass(ClientSpoofModule.class);
        if (module != null && module.isEnabled()) {
            cir.setReturnValue(module.getSpoofedBrand());
        }
    }
}