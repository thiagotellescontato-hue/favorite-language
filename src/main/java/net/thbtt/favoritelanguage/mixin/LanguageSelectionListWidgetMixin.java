package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import net.thbtt.favoritelanguage.client.FavoriteLanguageEntryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntryListWidget.class)
public abstract class LanguageSelectionListWidgetMixin {
    @Shadow
    public abstract List<?> children();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void favoritelanguage$toggleFavoriteFromStarColumn(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        for (Object entry : this.children()) {
            if (entry instanceof FavoriteLanguageEntryAccess favoriteEntry && favoriteEntry.favoritelanguage$toggleFavoriteAt(mouseX, mouseY, button)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
