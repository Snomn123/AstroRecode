package dev.recode.astro.screens.menu;

import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;

import java.util.HashSet;
import java.util.Set;

public final class ClickGuiState {

    public Category selectedCategory = Category.values()[0];
    public boolean configTabActive = false;
    public boolean friendsTabActive = false;

    public final Set<Module> expandedModules = new HashSet<>();

    public int configIcon = -1;
    public int friendsIcon = -1;

    public int themeColorPushes = 0;
    public int themeVarPushes = 0;
}