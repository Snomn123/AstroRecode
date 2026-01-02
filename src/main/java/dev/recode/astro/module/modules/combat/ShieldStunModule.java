package dev.recode.astro.module.modules.combat;

import dev.recode.astro.api.utils.InventoryUtil;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.RangeSliderSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;

public class ShieldStunModule extends Module {
    private final RangeSliderSetting switchDelay = new RangeSliderSetting("Switch Delay", 10, 50, 0, 250);
    private final RangeSliderSetting hitDelay = new RangeSliderSetting("Hit Delay", 10, 120, 0, 250);
    private final RangeSliderSetting switchBackDelay = new RangeSliderSetting("Switch Back Delay", 50, 100, 0, 250);

    private int step = 0;
    private long lastActionTime = 0;
    private int previousSlot = -1;
    private Player target = null;
    private long currentDelay = 0;

    public ShieldStunModule() {
        super("ShieldStun", Category.COMBAT);
        setDescription("auto stuns/breaks enemy shields");


        addSetting(switchDelay);
        addSetting(hitDelay);
        addSetting(switchBackDelay);

        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft mc) {
        if (mc.player == null || mc.level == null || mc.gameMode == null || mc.screen != null || !isEnabled()) return;


        if (step == 0) {
            scanForTarget(mc);
        }


        while (step > 0 && (System.currentTimeMillis() - lastActionTime) >= currentDelay) {
            int lastStep = step;
            processState(mc);
            if (step == lastStep) break;
        }
    }

    private void scanForTarget(Minecraft mc) {
        if (mc.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof Player p) {
            if (p.isBlocking() && p.isHolding(Items.SHIELD)) {
                target = p;
                previousSlot = InventoryUtil.getSelectedSlot(mc);

                if (mc.player.getMainHandItem().getItem() instanceof AxeItem) {
                    step = 2;
                    currentDelay = (long) hitDelay.getRandomDouble();
                } else {
                    step = 1;
                    currentDelay = (long) switchDelay.getRandomDouble();
                }
                lastActionTime = System.currentTimeMillis();
            }
        }
    }

    private void processState(Minecraft mc) {
        switch (step) {
            case 1: // Switch to Axe
                if (InventoryUtil.findAndSelectItem(mc, AxeItem.class)) {
                    step = 2;
                    currentDelay = (long) hitDelay.getRandomDouble();
                    lastActionTime = System.currentTimeMillis();
                } else {
                    reset();
                }
                break;

            case 2: // Attack
                if (isTargetValid(mc)) {
                    mc.gameMode.attack(mc.player, target);
                    mc.player.swing(InteractionHand.MAIN_HAND);
                    step = 3;
                    currentDelay = (long) switchBackDelay.getRandomDouble();
                    lastActionTime = System.currentTimeMillis();
                } else {
                    reset();
                }
                break;

            case 3: // Switch Back
                if (previousSlot != -1 && previousSlot != InventoryUtil.getSelectedSlot(mc)) {
                    InventoryUtil.setSelectedSlot(mc, previousSlot);
                }
                reset();
                break;
        }
    }

    private boolean isTargetValid(Minecraft mc) {
        if (target == null || target.isRemoved() || !target.isBlocking()) return false;
        return mc.hitResult instanceof EntityHitResult hit && hit.getEntity() == target;
    }

    private void reset() {
        step = 0;
        target = null;
        currentDelay = 0;
        previousSlot = -1;
    }

    @Override
    public void onEnable() { reset(); }

    @Override
    public void onDisable() { reset(); }
}
