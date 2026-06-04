package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.thbtt.favoritelanguage.client.FavoriteLanguageScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageOptionsScreenMixin extends OptionsSubScreen {
    @Shadow
    private LanguageManager languageManager;

    private LanguageOptionsScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "addFooter", at = @At("TAIL"))
    private void favoritelanguage$addFavoritesTab(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(Component.translatable("favoritelanguage.favorites"), button -> {
                    this.favoritelanguage$clearVanillaLanguageSelection();
                    this.minecraft.setScreen(new FavoriteLanguageScreen(this, this.options, this.languageManager));
                })
                .bounds(20, 8, 150, 20)
                .build());
    }

    @Unique
    private void favoritelanguage$clearVanillaLanguageSelection() {
        try {
            Field field = LanguageSelectScreen.class.getDeclaredField("languageSelectionList");
            field.setAccessible(true);
            ((AbstractSelectionList<?>) field.get(this)).setSelected(null);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
