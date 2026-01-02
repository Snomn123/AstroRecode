package dev.recode.astro.api.rotation;

import dev.recode.astro.api.utils.Globals;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class RotationUtils implements Globals {
    public static Vec2 getRotationAsVec2(Vec3 posTo, Vec3 posFrom) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }
// "322222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222""""""pacmbat;"""2222222222"""" - My cat who sat in my keyboard

    public static float[] getRotations(Vec3 vec3) {
        return getRotations(vec3.x, vec3.y, vec3.z);
    }

    public static float[] getRotations(double x, double y, double z) {
        Vec3 eyePos = MC.player.getEyePosition();

        double dx = x - eyePos.x;
        double dy = y - eyePos.y;
        double dz = z - eyePos.z;

        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        yaw = Mth.wrapDegrees(yaw);
        pitch = Mth.clamp(pitch, -90.0f, 90.0f);

        return new float[]{yaw, pitch};
    }

    public static double normalizeAngle(Double angleIn) {
        double angle = angleIn;
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static Vec2 getRotationFromVec(Vec3 vec) {
        double xz = Math.hypot(vec.x, vec.z);
        float yaw = (float) normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        float pitch = (float) normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
        return new Vec2(yaw, pitch);
    }

    public static float[] getRotationsTo(Vec3 start, Vec3 end) {
        Vec3 diff = end.subtract(start);
        float yaw = (float) (Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(diff.y, Math.hypot(diff.x, diff.z)));
        return new float[] {
                Mth.wrapDegrees(yaw),
                Mth.wrapDegrees(pitch)
        };
    }
}
