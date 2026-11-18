package com.stringduper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class StringDuperMod implements ModInitializer {
    public static int minStringDrop = 1;
    public static int maxStringDrop = 3;

    @Override
    public void onInitialize() {
        loadConfig();
    }

    /** config/string_duper.json (created on first launch). */
    private static void loadConfig() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("string_duper.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (Files.exists(path)) {
                JsonObject o = gson.fromJson(Files.readString(path), JsonObject.class);
                if (o.has("minStringDrop")) minStringDrop = o.get("minStringDrop").getAsInt();
                if (o.has("maxStringDrop")) maxStringDrop = o.get("maxStringDrop").getAsInt();
            } else {
                JsonObject o = new JsonObject();
                o.addProperty("minStringDrop", minStringDrop);
                o.addProperty("maxStringDrop", maxStringDrop);
                Files.writeString(path, gson.toJson(o));
            }
        } catch (Exception e) {
            System.err.println("[string_duper] config error, using defaults: " + e);
        }
        minStringDrop = Math.max(0, minStringDrop);
        maxStringDrop = Math.max(minStringDrop, maxStringDrop);
    }
}
