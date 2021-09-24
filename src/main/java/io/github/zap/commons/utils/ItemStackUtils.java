package io.github.zap.commons.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemStackUtils {
    public static boolean isEmpty(ItemStack stack) {
        return stack.getAmount() == 0 && stack.getType() == Material.AIR;
    }
}
