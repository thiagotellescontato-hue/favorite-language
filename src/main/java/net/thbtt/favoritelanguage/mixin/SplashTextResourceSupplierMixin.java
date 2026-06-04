package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SplashManager.class)
public class SplashTextResourceSupplierMixin {
    @Unique
    private static final List<Component> CUSTOM_SPLASH_TEXTS = List.of(
            Component.literal("ThBTT on Modrinth!")
    );

    @Shadow
    private List<Component> splashes;

    @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void favoritelanguage$addCustomSplashTexts(
            List<Component> prepared,
            ResourceManager manager,
            ProfilerFiller profiler,
            CallbackInfo ci
    ) {
        List<Component> splashes = new ArrayList<>(this.splashes);
        splashes.addAll(CUSTOM_SPLASH_TEXTS);
        this.splashes = splashes;
    }
}
