package com.noexperience;

import net.minecraftforge.fml.common.Mod;

/**
 * Mod entry point.
 *
 * <p>All behaviour lives in the event subscriber classes:
 * {@link ExperienceLock} (both sides, gameplay) and
 * {@link com.noexperience.client.ClientExperienceHud} (client, HUD).
 */
@Mod(NoExperience.MOD_ID)
public final class NoExperience {
    /** Mod id, must match the value in {@code gradle.properties} and {@code META-INF/mods.toml}. */
    public static final String MOD_ID = "no_experience";

    public NoExperience() {
        // No mod-bus setup needed: every handler is registered through @Mod.EventBusSubscriber.
    }
}
