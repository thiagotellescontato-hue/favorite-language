package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    /*
     * Copy checklist:
     * 1. Put this file in the target project's mixin package.
     * 2. Update the package line above if the target package is different.
     * 3. Add "SplashTextResourceSupplierMixin" to the client array in the target *.mixins.json.
     * 4. Edit CUSTOM_SPLASH_TEXTS below.
     */
    @Unique
    private static final List<String> CUSTOM_SPLASH_TEXTS = List.of(
            "ThBTT on Modrinth!"
    );

    @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("HEAD"))
    private void favoritelanguage$addCustomSplashTexts(
            List<String> prepared,
            ResourceManager manager,
            Profiler profiler,
            CallbackInfo ci
    ) {
        prepared.addAll(CUSTOM_SPLASH_TEXTS);
    }
}
