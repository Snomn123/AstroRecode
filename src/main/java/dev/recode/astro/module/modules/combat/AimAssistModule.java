package dev.recode.astro.module.modules.combat;

import dev.recode.astro.api.config.FriendCFG;
import dev.recode.astro.api.utils.Globals;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.RangeSliderSetting;
import dev.recode.astro.module.settings.SliderSetting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Random;

public class AimAssistModule extends Module implements Globals {

    private final RangeSliderSetting horizontalSpeed =
            new RangeSliderSetting("Horizontal speed", 3.0, 5.0, 0.5, 20.0);

    private final RangeSliderSetting verticalSpeed =
            new RangeSliderSetting("Vertical speed", 2.5, 4.5, 0.5, 20.0);

    private final SliderSetting maxDistance =
            new SliderSetting("Max distance", 4.5, 1.0, 8.0);

    private final SliderSetting maxFov =
            new SliderSetting("Max FOV", 90.0, 10.0, 360.0); // Now supports full circle

    private Player currentTarget;
    private final Random random = new Random();

    private float yawSpeed;
    private float pitchSpeed;
    private Vec3 aimOffset = Vec3.ZERO;
    private int speedTicks;
    private int offsetTicks;

    public AimAssistModule() {
        super("AimAssist", Category.COMBAT);
        setDescription("aims at players for you");

        addSetting(horizontalSpeed);
        addSetting(verticalSpeed);
        addSetting(maxDistance);
        addSetting(maxFov);

        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    @Override
    public void onEnable() {
        currentTarget = null;
        randomizeSpeed();
        randomizeOffset();
    }

    private void onTick(Minecraft mc) {
        if (!isEnabled() || mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }

        updateRandomization();

        Player player = mc.player;
        Player target = findTarget(player);

        if (target == null) {
            currentTarget = null;
            return;
        }

        currentTarget = target;


        Vec3 eyePos = player.getEyePosition();
        Vec3 targetPos = getTargetPos(target);

        Vec3 diff = targetPos.subtract(eyePos);
        double distXZ = Math.sqrt(diff.x * diff.x + diff.z * diff.z);

        float targetYaw = Mth.wrapDegrees((float) (Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0F));
        float targetPitch = Mth.wrapDegrees((float) (-Math.toDegrees(Math.atan2(diff.y, distXZ))));


        double sensitivity = mc.options.sensitivity().get();
        float f = (float) (sensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;

        float yawDiff = Mth.wrapDegrees(targetYaw - player.getYRot());
        float pitchDiff = Mth.wrapDegrees(targetPitch - player.getXRot());


        float yawStep = clamp(yawDiff, -yawSpeed, yawSpeed);
        float pitchStep = clamp(pitchDiff, -pitchSpeed, pitchSpeed);


        if (Math.abs(yawDiff) > 0.05) {
            float finalYawStep = Math.round(yawStep / gcd) * gcd;
            player.setYRot(player.getYRot() + finalYawStep);
        }

        if (Math.abs(pitchDiff) > 0.05) {
            float finalPitchStep = Math.round(pitchStep / gcd) * gcd;
            player.setXRot(Mth.clamp(player.getXRot() + finalPitchStep, -90.0F, 90.0F));
        }


        player.yHeadRot = player.getYRot();
        player.yBodyRot = player.getYRot();
    }

    private Player findTarget(Player player) {
        return player.level().players().stream()
                .filter(p -> p != player && p.isAlive() && !p.isSpectator())
                .filter(p -> !FriendCFG.isFriend(p.getName().getString()))
                .filter(p -> player.distanceTo(p) <= maxDistance.getDoubleValue())
                .filter(p -> {
                    if (maxFov.getFloatValue() >= 360.0F) return true;
                    float yawTo = getYawTo(player, p);
                    return Math.abs(Mth.wrapDegrees(yawTo - player.getYRot()))
                            <= maxFov.getFloatValue() / 2F;
                })
                .min(Comparator.comparingDouble(player::distanceTo))
                .orElse(null);
    }

    private Vec3 getTargetPos(Player target) {
        AABB bb = target.getBoundingBox();

        return new Vec3(
                (bb.minX + bb.maxX) * 0.5 + aimOffset.x,
                (bb.minY + bb.maxY) * 0.5 + 0.3 + aimOffset.y,
                (bb.minZ + bb.maxZ) * 0.5 + aimOffset.z
        );
    }

    private void updateRandomization() {
        if (--speedTicks <= 0) randomizeSpeed();
        if (--offsetTicks <= 0) randomizeOffset();
    }

    private void randomizeSpeed() {
        yawSpeed = (float) horizontalSpeed.getRandomDouble();
        pitchSpeed = (float) verticalSpeed.getRandomDouble();
        speedTicks = random.nextInt(10, 25);
    }

    private void randomizeOffset() {
        aimOffset = new Vec3(
                random.nextDouble(-0.1, 0.1),
                random.nextDouble(-0.1, 0.1),
                random.nextDouble(-0.1, 0.1)
        );
        offsetTicks = random.nextInt(15, 40);
    }

    private float getYawTo(Player from, Player to) {
        Vec3 diff = to.position().subtract(from.getEyePosition());
        return (float) (Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0F);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}