package net.thbtt.favoritelanguage.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Set;

public final class FavoriteLanguageScreen extends Screen {
    private final Screen parent;
    private final GameOptions gameOptions;
    private final LanguageManager languageManager;
    private FavoriteLanguageList list;

    public FavoriteLanguageScreen(Screen parent, GameOptions gameOptions, LanguageManager languageManager) {
        super(Text.translatable("favoritelanguage.favorites"));
        this.parent = parent;
        this.gameOptions = gameOptions;
        this.languageManager = languageManager;
    }

    @Override
    protected void init() {
        this.list = this.addDrawableChild(new FavoriteLanguageList(this.client, this.width, this.height - 64, 32, 18));
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("favoritelanguage.all_languages"), button -> this.client.setScreen(this.parent))
                .dimensions(this.width / 2 - 155, this.height - 27, 150, 20)
                .build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.applySelectedAndClose())
                .dimensions(this.width / 2 + 5, this.height - 27, 150, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFFFF);
        if (this.list.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("favoritelanguage.empty"), this.width / 2, this.height / 2, 0xFFA0A0A0);
        }
    }

    private void applySelectedAndClose() {
        FavoriteLanguageEntry selected = this.list.getSelectedOrNull();
        if (selected != null && !selected.languageCode.equals(this.languageManager.getLanguage())) {
            this.languageManager.setLanguage(selected.languageCode);
            this.gameOptions.language = selected.languageCode;
            this.client.reloadResources();
        }
        this.client.setScreen(this.parent);
    }

    private final class FavoriteLanguageList extends AlwaysSelectedEntryListWidget<FavoriteLanguageEntry> {
        private FavoriteLanguageList(MinecraftClient client, int width, int height, int y, int itemHeight) {
            super(client, width, height, y, itemHeight);
            Set<String> favorites = FavoriteLanguageStore.all();
            String currentLanguage = languageManager.getLanguage();
            for (Map.Entry<String, LanguageDefinition> language : languageManager.getAllLanguages().entrySet()) {
                if (!favorites.contains(language.getKey())) {
                    continue;
                }
                FavoriteLanguageEntry entry = new FavoriteLanguageEntry(this, language.getKey(), language.getValue());
                this.addEntry(entry);
                if (currentLanguage.equals(language.getKey())) {
                    this.setSelected(entry);
                }
            }
            FavoriteLanguageEntry selected = this.getSelectedOrNull();
            if (selected != null) {
                this.centerScrollOn(selected);
            }
        }

        public boolean isEmpty() {
            return this.getEntryCount() == 0;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }
    }

    private final class FavoriteLanguageEntry extends AlwaysSelectedEntryListWidget.Entry<FavoriteLanguageEntry> {
        private final FavoriteLanguageList list;
        private final String languageCode;
        private final Text languageDefinition;

        private FavoriteLanguageEntry(FavoriteLanguageList list, String languageCode, LanguageDefinition languageDefinition) {
            this.list = list;
            this.languageCode = languageCode;
            this.languageDefinition = languageDefinition.getDisplayText();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer renderer = FavoriteLanguageScreen.this.textRenderer;
            context.drawCenteredTextWithShadow(renderer, this.languageDefinition, FavoriteLanguageScreen.this.width / 2, y + entryHeight / 2 - 9 / 2, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.onPressed();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (net.minecraft.client.input.KeyCodes.isToggle(keyCode)) {
                this.onPressed();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void onPressed() {
            this.list.setSelected(this);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.languageDefinition);
        }
    }
}

