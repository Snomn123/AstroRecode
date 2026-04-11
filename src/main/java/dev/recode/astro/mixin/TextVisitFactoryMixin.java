package dev.recode.astro.mixin;

import dev.recode.astro.module.modules.misc.StreamerModule;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StringDecomposer.class)
public class TextVisitFactoryMixin {

    @ModifyVariable(
            method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private static String protectUsernameInText(String text) {
        StreamerModule module = StreamerModule.getInstance();
        if (module != null && module.isEnabled()) {
            return module.hiddenUser(text);
        }
        return text;
    }
}