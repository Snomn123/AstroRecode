package dev.recode.astro.api.utils;

import dev.recode.astro.mixin.accessor.InventoryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventoryUtil {


    public static int getSelectedSlot(Minecraft client) {
        if (client.player == null) return -1;
        return ((InventoryAccessor) client.player.getInventory()).getSelected();
    }


    public static void setSelectedSlot(Minecraft client, int slot) {
        if (client.player == null || slot < 0 || slot > 8) return;
        ((InventoryAccessor) client.player.getInventory()).setSelected(slot);
    }


    public static int findItemInHotbar(Minecraft client, Class<? extends Item> itemClass) {
        if (client.player == null) return -1;

        Inventory inventory = client.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }


    public static boolean findAndSelectItem(Minecraft client, Class<? extends Item> itemClass) {
        int slot = findItemInHotbar(client, itemClass);
        if (slot != -1) {
            setSelectedSlot(client, slot);
            return true;
        }
        return false;
    }
}