package dev.recode.astro.mixin;

import dev.recode.astro.module.ModuleManager;
import dev.recode.astro.module.modules.misc.NoJumpDelay;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Shadow
    private int noJumpDelay;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        NoJumpDelay module = ModuleManager.getInstance().getModuleByClass(NoJumpDelay.class);
        if (module != null && module.isEnabled()) {
            this.noJumpDelay = 0;
        }
    }
}