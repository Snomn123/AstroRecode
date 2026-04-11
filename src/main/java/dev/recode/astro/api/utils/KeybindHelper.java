package dev.recode.astro.api.utils;

import org.lwjgl.glfw.GLFW;

public final class KeybindHelper {

    private KeybindHelper() {}

    public static String getKeyName(int code) {
        if (code == 0) return "None";

        return switch (code) {
            case GLFW.GLFW_KEY_ESCAPE -> "Esc";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LShift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RShift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCtrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCtrl";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LAlt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RAlt";
            case GLFW.GLFW_KEY_SPACE -> "Space";


            case GLFW.GLFW_MOUSE_BUTTON_1 -> "LMB";
            case GLFW.GLFW_MOUSE_BUTTON_2 -> "RMB";
            case GLFW.GLFW_MOUSE_BUTTON_3 -> "MMB";

            default -> {
                String name = GLFW.glfwGetKeyName(code, 0);
                yield name != null ? name.toUpperCase() : "Key " + code;
            }
        };
    }
}
