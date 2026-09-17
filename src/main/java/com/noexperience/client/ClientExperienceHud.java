package com.noexperience.client;

import com.noexperience.NoExperience;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client half of the mod: the experience bar is never drawn.
 *
 * <p>On 1.20.1 the whole bar (background, progress and the level number) is rendered by the
 * {@code experience_bar} overlay, so cancelling that one overlay hides all of it. The jump bar
 * while riding a horse stays untouched.
 */
@Mod.EventBusSubscriber(modid = NoExperience.MOD_ID, value = Dist.CLIENT)
public final class ClientExperienceHud {
    private ClientExperienceHud() {}

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (VanillaGuiOverlay.EXPERIENCE_BAR.id().equals(event.getOverlay().id())) {
            event.setCanceled(true);
        }
    }
}
