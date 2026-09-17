package com.noexperience.client;

import com.noexperience.NoExperience;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Client half of the mod: the experience bar is never drawn.
 *
 * <p>On 1.21.1 the bar and the level number are two separate HUD layers, so both are cancelled.
 * The jump meter layer of a ridden horse stays untouched.
 */
@EventBusSubscriber(modid = NoExperience.MOD_ID, value = Dist.CLIENT)
public final class ClientExperienceHud {
    private ClientExperienceHud() {}

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        ResourceLocation name = event.getName();
        if (VanillaGuiLayers.EXPERIENCE_BAR.equals(name) || VanillaGuiLayers.EXPERIENCE_LEVEL.equals(name)) {
            event.setCanceled(true);
        }
    }
}
