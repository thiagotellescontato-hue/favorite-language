package net.thbtt.favoritelanguage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.Set;

public final class FavoriteLanguageScreen extends Screen {
    private final Screen parent;
    private final Options options;
    private final LanguageManager languageManager;
    private FavoriteLanguageList list;

    public FavoriteLanguageScreen(Screen parent, Options options, LanguageManager languageManager) {
        super(Component.translatable("favoritelanguage.favorites"));
        this.parent = parent;
        this.options = options;
        this.languageManager = languageManager;
    }

    @Override
    protected void init() {
        this.list = this.addRenderableWidget(new FavoriteLanguageList(this.minecraft, this.width, this.height - 64, 32, 18));
        this.addRenderableWidget(Button.builder(Component.translatable("favoritelanguage.all_languages"), button -> this.minecraft.setScreen(this.parent))
                .bounds(this.width / 2 - 155, this.height - 27, 150, 20)
                .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.applySelectedAndClose())
                .bounds(this.width / 2 + 5, this.height - 27, 150, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        if (this.list.isEmpty()) {
            graphics.centeredText(this.font, Component.translatable("favoritelanguage.empty"), this.width / 2, this.height / 2, 0xFFA0A0A0);
        }
    }

    private void applySelectedAndClose() {
        FavoriteLanguageEntry selected = this.list.getSelected();
        if (selected != null && !selected.languageCode.equals(this.languageManager.getSelected())) {
            this.languageManager.setSelected(selected.languageCode);
            this.options.languageCode = selected.languageCode;
            this.minecraft.reloadResourcePacks();
        }
        this.minecraft.setScreen(this.parent);
    }

    private final class FavoriteLanguageList extends ObjectSelectionList<FavoriteLanguageEntry> {
        private FavoriteLanguageList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            Set<String> favorites = FavoriteLanguageStore.all();
            String currentLanguage = languageManager.getSelected();
            for (Map.Entry<String, LanguageInfo> language : languageManager.getLanguages().entrySet()) {
                if (!favorites.contains(language.getKey())) {
                    continue;
                }
                FavoriteLanguageEntry entry = new FavoriteLanguageEntry(this, language.getKey(), language.getValue());
                this.addEntry(entry);
                if (currentLanguage.equals(language.getKey())) {
                    this.setSelected(entry);
                }
            }
            FavoriteLanguageEntry selected = this.getSelected();
            if (selected != null) {
                this.centerScrollOn(selected);
            }
        }

        public boolean isEmpty() {
            return this.children().isEmpty();
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }
    }

    private final class FavoriteLanguageEntry extends ObjectSelectionList.Entry<FavoriteLanguageEntry> {
        private final FavoriteLanguageList list;
        private final String languageCode;
        private final Component languageDefinition;

        private FavoriteLanguageEntry(FavoriteLanguageList list, String languageCode, LanguageInfo languageInfo) {
            this.list = list;
            this.languageCode = languageCode;
            this.languageDefinition = languageInfo.toComponent();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Font renderer = FavoriteLanguageScreen.this.font;
            graphics.centeredText(renderer, this.languageDefinition, FavoriteLanguageScreen.this.width / 2, this.getContentYMiddle() - 9 / 2, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
            this.onPressed();
            return true;
        }

        private void onPressed() {
            this.list.setSelected(this);
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", this.languageDefinition);
        }
    }
}
