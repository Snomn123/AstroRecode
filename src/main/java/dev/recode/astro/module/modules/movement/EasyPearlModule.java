package dev.recode.astro.module.modules.movement;

import dev.recode.astro.api.utils.InventoryUtil;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.RangeSliderSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class EasyPearlModule extends Module {
    private final RangeSliderSetting switchDelay;
    private final RangeSliderSetting throwDelay;
    private final RangeSliderSetting switchBackDelay;

    private int step = 0;
    private long time = 0;
    private int previousSlot = -1;

    public EasyPearlModule() {
        super("EasyPearl", Category.MOVEMENT);
        setDescription("Swaps to an ender pearl, throws it, then swaps back on toggle.");

        switchDelay = new RangeSliderSetting("switch delay", 10, 50, 0, 250);
        throwDelay = new RangeSliderSetting("throw delay", 10, 50, 0, 250);
        switchBackDelay = new RangeSliderSetting("switch back delay", 50, 70, 0, 250);

        addSetting(switchDelay);
        addSetting(throwDelay);
        addSetting(switchBackDelay);

        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    @Override
    public void onEnable() {
        Minecraft client = Minecraft.getInstance();
        if (!canRun(client)) {
            setEnabled(false);
            return;
        }

        previousSlot = InventoryUtil.getSelectedSlot(client);
        step = 1;
        time = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        reset();
    }

    private void onTick(Minecraft client) {
        if (!isEnabled() || !canRun(client)) return;

        switch (step) {
            case 1 -> switchToPearl(client);
            case 2 -> throwPearl(client);
            case 3 -> switchBack(client);
        }
    }

    private boolean canRun(Minecraft client) {
        return client.player != null && client.level != null &&
                client.gameMode != null && client.screen == null;
    }

    private void switchToPearl(Minecraft client) {
        if (!hasDelayPassed((long) switchDelay.getRandomDouble())) return;

        if (InventoryUtil.findAndSelectItem(client, Items.ENDER_PEARL.getClass())) {
            step = 2;
            time = System.currentTimeMillis();
        } else {
            setEnabled(false);
        }
    }

    private void throwPearl(Minecraft client) {
        if (!hasDelayPassed((long) throwDelay.getRandomDouble())) return;

        if (!client.player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.ENDER_PEARL)) {
            setEnabled(false);
            return;
        }

        client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
        step = 3;
        time = System.currentTimeMillis();
    }

    private void switchBack(Minecraft client) {
        if (!hasDelayPassed((long) switchBackDelay.getRandomDouble())) return;

        if (previousSlot != -1 && previousSlot != InventoryUtil.getSelectedSlot(client)) {
            InventoryUtil.setSelectedSlot(client, previousSlot);
        }

        setEnabled(false);
    }

    private boolean hasDelayPassed(long delay) {
        return System.currentTimeMillis() - time >= delay;
    }

    private void reset() {
        step = 0;
        time = 0;
        previousSlot = -1;
    }
}