package dev.recode.astro.screens.menu.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class FriendCFG {

    private static final Path CONFIG_DIR = Path.of("astro/cfg/friends");
    private static final Path FRIENDS_FILE = CONFIG_DIR.resolve("friends.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ImString newFriendName = new ImString(69);
    private static final Set<String> friends = new HashSet<>();

    static {
        load();
        newFriendName.set("user"); // default, this is pretty much needed (if there is no text there its hard to tell there is a input field)
    }

    public static void addFriend(String name) {
        if (name == null || name.trim().isEmpty()) return;
        friends.add(name.trim().toLowerCase());
        save();
    }

    public static void removeFriend(String name) {
        if (name == null || name.trim().isEmpty()) return;
        friends.remove(name.toLowerCase());
        save();
    }

    public static boolean isFriend(String name) {
        return name != null && friends.contains(name.toLowerCase());
    }

    public static Set<String> getFriends() {
        return new HashSet<>(friends);
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            JsonObject obj = new JsonObject();
            for (String f : friends) obj.addProperty(f, true);
            Files.writeString(FRIENDS_FILE, GSON.toJson(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        friends.clear();
        if (!Files.exists(FRIENDS_FILE)) return;

        try {
            String json = Files.readString(FRIENDS_FILE);
            JsonElement root = GSON.fromJson(json, JsonElement.class);
            if (root == null || !root.isJsonObject()) return;

            for (String key : root.getAsJsonObject().keySet()) {
                friends.add(key.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void renderFriends(int accentColor, int textColor) {
        ImGui.textColored(accentColor, "Friends");
        ImGui.separator();

        float inputWidth = 220;
        float addButtonWidth = 80;
        float addButtonHeight = 28;

        ImGui.pushItemWidth(inputWidth);
        ImGui.alignTextToFramePadding();
        ImGui.inputText("##NewFriend", newFriendName);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Button, accentColor);
        if (ImGui.button("+ Add", addButtonWidth, addButtonHeight)) {
            String name = newFriendName.get().trim();
            if (!name.isEmpty()) {
                addFriend(name);
                newFriendName.set(""); // empty since yk already know there is a input field
            }
        }
        ImGui.popStyleColor();

        List<String> friendList = new ArrayList<>(getFriends());
        Collections.sort(friendList, Collections.reverseOrder());

        if (friendList.isEmpty()) {
            ImGui.textColored(0xFF888888, "you have no friends :(");
            return;
        }

        for (int i = 0; i < friendList.size(); i++) {
            String friend = friendList.get(i);
            float buttonWidth = 60;
            float buttonHeight = 18;


            float availWidth = ImGui.getContentRegionAvail().x;
            ImGui.setCursorPosX(ImGui.getCursorPosX() + availWidth - buttonWidth);

            ImGui.pushStyleColor(ImGuiCol.Button, accentColor);
            if (ImGui.button("Remove##" + friend, buttonWidth, buttonHeight)) {
                removeFriend(friend);
            }
            ImGui.popStyleColor();


            ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getCursorPosX() - availWidth);
            ImGui.setCursorPosY(ImGui.getCursorPosY() + buttonHeight - ImGui.getTextLineHeight());
            ImGui.text(friend);


            if (i < friendList.size() - 1) ImGui.dummy(0, 1);
        }
    }
}