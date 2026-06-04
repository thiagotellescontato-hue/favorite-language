package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.text.Text;
import net.thbtt.favoritelanguage.client.FavoriteLanguageScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen {
    @Shadow
    LanguageManager languageManager;

    private LanguageOptionsScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "initFooter", at = @At("TAIL"))
    private void favoritelanguage$addFavoritesTab(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("favoritelanguage.favorites"), button -> {
                    this.favoritelanguage$clearVanillaLanguageSelection();
                    this.client.setScreen(new FavoriteLanguageScreen(this, this.gameOptions, this.languageManager));
                })
                .dimensions(20, 8, 150, 20)
                .build());
    }

    @Unique
    private void favoritelanguage$clearVanillaLanguageSelection() {
        for (String fieldName : new String[]{"languageSelectionList", "field_2486"}) {
            try {
                Field field = LanguageOptionsScreen.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                ((EntryListWidget<?>) field.get(this)).setSelected(null);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }
}

