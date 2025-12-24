package dev.recode.astro.api.utils;

public final class ColorHelper {

    private ColorHelper() {

    }

    public static float[] intToFloatArray(int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new float[]{r, g, b, a};
    }




    public static int floatArrayToInt(float[] color) {
        int r = (int) (color[0] * 255) & 0xFF;
        int g = (int) (color[1] * 255) & 0xFF;
        int b = (int) (color[2] * 255) & 0xFF;
        int a = (int) (color[3] * 255) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }


     //ARGB to ABGR format

    public static int argbToAbgr(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}
