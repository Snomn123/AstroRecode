package dev.recode.astro.module.modules.combat;

import dev.recode.astro.api.event.events.ClientRenderEvent;
import dev.recode.astro.api.event.orbit.EventHandler;
import dev.recode.astro.api.utils.OrbitManager;
import dev.recode.astro.mixin.imgui.GameRendererMixin;
import dev.recode.astro.screens.menu.other.FriendCFG;
import dev.recode.astro.api.utils.Globals;
import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.RangeSliderSetting;
import dev.recode.astro.module.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Random;

/**
 * some concept stuff for aim module.
 * since it hasn't been switched to using RotationUtil i figured why not make it ClientRender based instead of tick based.
 * @see GameRendererMixin
 * @see ClientRenderEvent
 * pretty garbage concept code as im working in a suboptimal place at the moment.
 */
public class AimAssistModule extends Module implements Globals {

    private final RangeSliderSetting horizontalSpeed;
    private final RangeSliderSetting verticalSpeed;
    private final SliderSetting maxDistance;
    private final SliderSetting maxFov;

    private final Random random = new Random();
    private float yawSpeed;
    private float pitchSpeed;
    private Vec3 aimOffset = Vec3.ZERO;
    private float randomizationTicksLeft;
    private float lastPartialTick;
    private boolean hasLastPartialTick;
    private float yawCarry;
    private float pitchCarry;

    public AimAssistModule() {
        super("AimAssist", Category.COMBAT);
        setDescription("aims at players for you");

        horizontalSpeed = new RangeSliderSetting("Horizontal speed", 3.0, 5.0, 0.5, 20.0);
        horizontalSpeed.setDescription("speed for aiming");

        verticalSpeed = new RangeSliderSetting("Vertical speed", 2.5, 4.5, 0.5, 20.0);
        verticalSpeed.setDescription("speed for aiming upwards and downwards");

        maxDistance = new SliderSetting("Max distance", 4.5, 1.0, 8.0);
        maxDistance.setDescription("The maximum reach the assist will lock onto.");

        maxFov = new SliderSetting("Max FOV", 90.0, 10.0, 360.0);
        maxFov.setDescription("The field of view range for target detection.");

        addSetting(horizontalSpeed);
        addSetting(verticalSpeed);
        addSetting(maxDistance);
        addSetting(maxFov);
    }

    @Override
    public void onEnable() {
        OrbitManager.EVENT_BUS.subscribe(this);
        randomize();
        resetDeltaState();
    }

    @Override
    public void onDisable() {
        OrbitManager.EVENT_BUS.unsubscribe(this);
        resetDeltaState();
    }

    @EventHandler
    public void onRender(ClientRenderEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (!isEnabled() || mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }

        float deltaTicks = getDeltaTicks(event);
        if (deltaTicks <= 0.0F) {
            return;
        }

        updateRandomization(deltaTicks);

        Player player = mc.player;
        Player target = findTarget(player);

        if (target == null) {
            yawCarry = 0.0F;
            pitchCarry = 0.0F;
            return;
        }

        Vec3 eyePos = player.getEyePosition();
        Vec3 targetPos = getTargetPos(target);

        Vec3 diff = targetPos.subtract(eyePos);
        double distXZ = Math.sqrt(diff.x * diff.x + diff.z * diff.z);

        float targetYaw = Mth.wrapDegrees((float) (Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0F));
        float targetPitch = Mth.wrapDegrees((float) (-Math.toDegrees(Math.atan2(diff.y, distXZ))));

        float yawDiff = Mth.wrapDegrees(targetYaw - player.getYRot());
        float pitchDiff = Mth.wrapDegrees(targetPitch - player.getXRot());

        float yawStep = ((yawDiff / 4.0F) * (yawSpeed / 5.0F)) * deltaTicks;
        float pitchStep = ((pitchDiff / 4.0F) * (pitchSpeed / 5.0F)) * deltaTicks;

        double sensitivity = mc.options.sensitivity().get();
        float f = (float) (sensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;

        if (Math.abs(yawDiff) > 0.05) {
            yawCarry += yawStep;
            float finalYawStep = Math.round(yawCarry / gcd) * gcd;
            player.setYRot(player.getYRot() + finalYawStep);
            yawCarry -= finalYawStep;
        } else {
            yawCarry = 0.0F;
        }

        if (Math.abs(pitchDiff) > 0.05) {
            pitchCarry += pitchStep;
            float finalPitchStep = Math.round(pitchCarry / gcd) * gcd;
            player.setXRot(Mth.clamp(player.getXRot() + finalPitchStep, -90.0F, 90.0F));
            pitchCarry -= finalPitchStep;
        } else {
            pitchCarry = 0.0F;
        }

        player.yHeadRot = player.getYRot();
        player.yBodyRot = player.getYRot();
    }

    private Player findTarget(Player player) {
        return player.level().players().stream()
                .filter(p -> p != player && p.isAlive() && !p.isSpectator())
                .filter(p -> !FriendCFG.isFriend(p.getName().getString()))
                .filter(p -> player.distanceTo(p) <= maxDistance.getDoubleValue())
                .filter(p -> isInFov(player, p))
                .min(Comparator.comparingDouble(player::distanceTo))
                .orElse(null);
    }

    private boolean isInFov(Player player, Player target) {
        if (maxFov.getFloatValue() >= 360.0F) return true;
        Vec3 diff = target.position().subtract(player.getEyePosition());
        float yawTo = (float) (Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0F);
        float fovDiff = Math.abs(Mth.wrapDegrees(yawTo - player.getYRot()));
        return fovDiff <= maxFov.getFloatValue() / 2F;
    }

    private Vec3 getTargetPos(Player target) {
        AABB bb = target.getBoundingBox();
        return new Vec3(
                (bb.minX + bb.maxX) * 0.5 + aimOffset.x,
                (bb.minY + bb.maxY) * 0.5 + 0.2 + aimOffset.y,
                (bb.minZ + bb.maxZ) * 0.5 + aimOffset.z
        );
    }

    private float getDeltaTicks(ClientRenderEvent event) {
        float partialTick = Mth.clamp(event.partialTick(), 0.0F, 1.0F);
        if (!hasLastPartialTick) {
            hasLastPartialTick = true;
            lastPartialTick = partialTick;
            return 0.0F;
        }

        float deltaTicks = partialTick - lastPartialTick;
        if (deltaTicks < 0.0F) {
            deltaTicks += 1.0F;
        }

        lastPartialTick = partialTick;
        return deltaTicks;
    }

    private void updateRandomization(float deltaTicks) {
        randomizationTicksLeft -= deltaTicks;
        if (randomizationTicksLeft <= 0.0F) randomize();
    }

    private void resetDeltaState() {
        hasLastPartialTick = false;
        lastPartialTick = 0.0F;
        yawCarry = 0.0F;
        pitchCarry = 0.0F;
    }

    private void randomize() {
        yawSpeed = (float) horizontalSpeed.getRandomDouble();
        pitchSpeed = (float) verticalSpeed.getRandomDouble();
        aimOffset = new Vec3(random.nextDouble(-0.1, 0.1), random.nextDouble(-0.1, 0.1), random.nextDouble(-0.1, 0.1));
        randomizationTicksLeft = random.nextInt(15, 45);
    }
}
