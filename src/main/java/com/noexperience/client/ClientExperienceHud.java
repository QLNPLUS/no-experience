package com.noexperience.client;

import com.noexperience.NoExperience;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Client half of the mod: the experience bar is never drawn.
 *
 * <p>26.1.2 no longer has a dedicated experience bar layer. The bar is part of the contextual info
 * bar, which the game shares between the experience bar, the locator bar and the jump bar of a
 * ridden vehicle. That choice is mirrored here (see {@code Gui#nextContextualInfoState}) so only
 * the experience case is suppressed and the locator and jump bars keep working. The level number
 * lives in its own layer and is always hidden.
 */
@EventBusSubscriber(modid = NoExperience.MOD_ID, value = Dist.CLIENT)
public final class ClientExperienceHud {
    private ClientExperienceHud() {}

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        Identifier name = event.getName();

        if (VanillaGuiLayers.EXPERIENCE_LEVEL.equals(name)) {
            event.setCanceled(true);
            return;
        }

        boolean contextualBar = VanillaGuiLayers.CONTEXTUAL_INFO_BAR.equals(name)
                || VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND.equals(name);
        if (contextualBar && showsExperienceBar()) {
            event.setCanceled(true);
        }
    }

    /** True while the contextual info bar would be showing the experience bar instead of something else. */
    private static boolean showsExperienceBar() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || !minecraft.gameMode.hasExperience()) {
            return false;
        }

        PlayerRideableJumping jumpable = player.jumpableVehicle();
        boolean hasWaypoints = minecraft.getConnection() != null
                && minecraft.getConnection().getWaypointManager().hasWaypoints();

        if (hasWaypoints) {
            if (jumpable != null && (player.getJumpRidingScale() > 0.0F || jumpable.getJumpCooldown() > 0)) {
                // The jump bar takes priority over the locator and experience bars.
                return false;
            }
            // Waypoints are shown unless the short window after an experience change wins.
            return isExperienceDisplayWindow(player);
        }

        // Without waypoints only a ridden jumpable vehicle can take the bar away from experience.
        return jumpable == null;
    }

    private static boolean isExperienceDisplayWindow(LocalPlayer player) {
        return player.experienceDisplayStartTick + 100 > player.tickCount;
    }
}
