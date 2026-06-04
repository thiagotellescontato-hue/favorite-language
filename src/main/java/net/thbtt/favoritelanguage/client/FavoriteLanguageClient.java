package net.thbtt.favoritelanguage.client;

import net.fabricmc.api.ClientModInitializer;
import net.thbtt.favoritelanguage.FavoriteLanguage;

public final class FavoriteLanguageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FavoriteLanguage.LOGGER.info("Favorite Language loaded.");
    }
}
