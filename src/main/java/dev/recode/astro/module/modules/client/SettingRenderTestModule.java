package dev.recode.astro.module.modules.client;

import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.*;

import java.util.List;

public class SettingRenderTestModule extends Module {

    public final ColorSetting testColor;
    public final ModeSetting testMode;
    public final MultiButtonSetting testMulti;
    public final StringSetting testString;
    public final BooleanSetting testBoolean;

    public final SliderSetting testSliderInt;
    public final SliderSetting testSliderFloat;
    public final SliderSetting testSliderDouble;

    public final RangeSliderSetting testRangeInt;
    public final RangeSliderSetting testRangeFloat;
    public final RangeSliderSetting testRangeDouble;

    private static final int MODULE_COLOR = 0xFF6969FF;

    public SettingRenderTestModule() {
        super("RenderTest", Category.CLIENT);
        setDescription("if you see this as a non developer, well you're not supposed so see this so go off this module ty :)");

        addSetting(new SeparatorSetting("Colors"));
        testColor = new ColorSetting("TestColor", 0xFF696969, MODULE_COLOR);
        addSetting(testColor);

        addSetting(new SeparatorSetting("modes (multibotton & mode)"));
        testMode = new ModeSetting("TestMode", List.of("Test1", "Test2", "Test3"), 0);
        testMulti = new MultiButtonSetting("TestMulti", List.of("Test1", "Test2", "Test3"));
        addSetting(testMode);
        addSetting(testMulti);

        addSetting(new SeparatorSetting("string setting"));
        testString = new StringSetting("TestString", "TestString");
        addSetting(testString);

        addSetting(new SeparatorSetting("boolean setting"));
        testBoolean = new BooleanSetting("TestBoolean", true);
        addSetting(testBoolean);

        addSetting(new SeparatorSetting("slider settings"));
        testSliderInt = new SliderSetting("TestSliderInt", 50, 0, 100);
        testSliderFloat = new SliderSetting("TestSliderFloat", 0.5f, 0.0f, 1.0f, "%.2f");
        testSliderDouble = new SliderSetting("TestSliderDouble", 2.5, 0.0f, 5.0f, "%.1f");
        addSetting(testSliderInt);
        addSetting(testSliderFloat);
        addSetting(testSliderDouble);

        addSetting(new SeparatorSetting("range slider settings"));
        testRangeInt = new RangeSliderSetting("TestRangeInt", 25, 75, 0, 100);
        testRangeFloat = new RangeSliderSetting("TestRangeFloat", 0.2f, 0.8f, 0.0f, 1.0f, "%.2f");
        testRangeDouble = new RangeSliderSetting("TestRangeDouble", 1.0, 4.0, 0.0, 5.0, "%.1f");
        addSetting(testRangeInt);
        addSetting(testRangeFloat);
        addSetting(testRangeDouble);
    }
}