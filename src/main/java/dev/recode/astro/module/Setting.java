package dev.recode.astro.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Setting {
    private final String name;
    private String description = "";

    public Setting(String name) {
        this.name = name;
    }

    public Setting setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    public String getName() {
        return name;
    }

    public abstract void render();

    public JsonElement serialize() {
        JsonObject data = new JsonObject();
        return data;
    }

    public void deserialize(JsonElement data) {
    }
}
