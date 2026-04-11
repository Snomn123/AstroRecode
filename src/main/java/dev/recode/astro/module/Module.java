package dev.recode.astro.module;

import dev.recode.astro.module.settings.KeybindSetting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private final String name;
    private final Category category;
    private String description = "";
    private boolean enabled = false;
    private final List<Setting> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this(name, category, 0);
    }

    public Module(String name, Category category, int defaultKey) {
        this.name = name;
        this.category = category;
        addSetting(new KeybindSetting("Keybind", defaultKey, KeybindMode.TOGGLE));
    }

    public Module setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void addSetting(Setting setting) {
        settings.add(setting);
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void onEnable() {}
    public void onDisable() {}
}