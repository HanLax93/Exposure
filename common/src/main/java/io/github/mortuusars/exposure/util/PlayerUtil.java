package io.github.mortuusars.exposure.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class PlayerUtil {
    private PlayerUtil() {
    }

    public static void displayClientMessage(Player player, Component message, boolean overlay) {
        if (overlay) {
            player.sendOverlayMessage(message);
        } else {
            player.sendSystemMessage(message);
        }
    }
}
