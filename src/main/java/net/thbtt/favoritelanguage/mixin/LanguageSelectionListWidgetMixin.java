package net.thbtt.favoritelanguage.mixin;

import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.thbtt.favoritelanguage.client.FavoriteLanguageEntryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerWidget.class)
public abstract class LanguageSelectionListWidgetMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void favoritelanguage$toggleFavoriteFromStarColumn(MouseButtonEvent click, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof AbstractSelectionList<?> list)) {
            return;
        }
        for (Object entry : list.children()) {
            if (entry instanceof FavoriteLanguageEntryAccess favoriteEntry && favoriteEntry.favoritelanguage$toggleFavoriteAt(click.x(), click.y(), click.button())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
