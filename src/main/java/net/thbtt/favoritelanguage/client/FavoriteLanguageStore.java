package net.thbtt.favoritelanguage.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.thbtt.favoritelanguage.FavoriteLanguage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class FavoriteLanguageStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("favoritelanguage.json");
    private static final Set<String> FAVORITES = new LinkedHashSet<>();
    private static boolean loaded;

    private FavoriteLanguageStore() {
    }

    public static synchronized Set<String> all() {
        load();
        return Collections.unmodifiableSet(new LinkedHashSet<>(FAVORITES));
    }

    public static synchronized boolean isFavorite(String languageCode) {
        load();
        return FAVORITES.contains(languageCode);
    }

    public static synchronized void toggle(String languageCode) {
        load();
        if (!FAVORITES.add(languageCode)) {
            FAVORITES.remove(languageCode);
        }
        save();
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonElement root = JsonParser.parseReader(reader);
            JsonArray favorites = root.getAsJsonObject().getAsJsonArray("favorites");
            if (favorites == null) {
                return;
            }
            for (JsonElement favorite : favorites) {
                if (favorite.isJsonPrimitive()) {
                    FAVORITES.add(favorite.getAsString());
                }
            }
        } catch (RuntimeException | IOException exception) {
            FavoriteLanguage.LOGGER.warn("Could not read favorite languages config", exception);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            JsonArray favorites = new JsonArray();
            FAVORITES.forEach(favorites::add);
            com.google.gson.JsonObject root = new com.google.gson.JsonObject();
            root.add("favorites", favorites);
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException exception) {
            FavoriteLanguage.LOGGER.warn("Could not save favorite languages config", exception);
        }
    }
}
