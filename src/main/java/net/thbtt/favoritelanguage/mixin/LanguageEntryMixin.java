package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.thbtt.favoritelanguage.FavoriteLanguage;
import net.thbtt.favoritelanguage.client.FavoriteLanguageEntryAccess;
import net.thbtt.favoritelanguage.client.FavoriteLanguageStore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry")
public abstract class LanguageEntryMixin extends AlwaysSelectedEntryListWidget.Entry implements FavoriteLanguageEntryAccess {
    @Unique
    private static final Identifier FAVORITELANGUAGE_EMPTY_STAR_TEXTURE = Identifier.of(FavoriteLanguage.MOD_ID, "textures/gui/star_empty.png");
    @Unique
    private static final Identifier FAVORITELANGUAGE_FAVORITE_STAR_TEXTURE = Identifier.of(FavoriteLanguage.MOD_ID, "textures/gui/star_favorite.png");
    @Unique
    private static final int FAVORITELANGUAGE_STAR_SIZE = 10;
    @Unique
    private static final int FAVORITELANGUAGE_STAR_TEXTURE_SIZE = 20;
    @Unique
    private static final int FAVORITELANGUAGE_STAR_LEFT_OFFSET = 14;

    @Shadow
    @Final
    String languageCode;

    @Unique
    private int favoritelanguage$starX;
    @Unique
    private int favoritelanguage$starY;

    @Inject(method = "render", at = @At("TAIL"))
    private void favoritelanguage$renderStar(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        this.favoritelanguage$starX = this.getX() - FAVORITELANGUAGE_STAR_LEFT_OFFSET;
        this.favoritelanguage$starY = this.getContentY() + (this.getContentHeight() - FAVORITELANGUAGE_STAR_SIZE) / 2;
        Identifier texture = FavoriteLanguageStore.isFavorite(this.languageCode)
                ? FAVORITELANGUAGE_FAVORITE_STAR_TEXTURE
                : FAVORITELANGUAGE_EMPTY_STAR_TEXTURE;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, this.favoritelanguage$starX, this.favoritelanguage$starY, 0.0F, 0.0F, FAVORITELANGUAGE_STAR_SIZE, FAVORITELANGUAGE_STAR_SIZE, FAVORITELANGUAGE_STAR_TEXTURE_SIZE, FAVORITELANGUAGE_STAR_TEXTURE_SIZE);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void favoritelanguage$toggleFavorite(Click click, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (this.favoritelanguage$toggleFavoriteAt(click.x(), click.y(), click.button())) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean favoritelanguage$toggleFavoriteAt(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX < this.favoritelanguage$starX || mouseX >= this.favoritelanguage$starX + FAVORITELANGUAGE_STAR_SIZE) {
            return false;
        }
        if (mouseY < this.favoritelanguage$starY || mouseY >= this.favoritelanguage$starY + FAVORITELANGUAGE_STAR_SIZE) {
            return false;
        }
        FavoriteLanguageStore.toggle(this.languageCode);
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
}
