package com.noexperience;

import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Gameplay half of the mod. Every experience source is closed off, and a per-tick fallback keeps
 * the stored experience of every player at exactly zero.
 *
 * <p>Covered gain paths:
 * <ul>
 *     <li>mob / player / boss XP drops ({@link LivingExperienceDropEvent});</li>
 *     <li>every direct grant, e.g. mining ore, smelting, fishing, trading and brewing
 *         ({@link PlayerXpEvent.XpChange});</li>
 *     <li>direct level grants such as {@code /xp add ... levels} ({@link PlayerXpEvent.LevelChange});</li>
 *     <li>experience orb pickups ({@link PlayerXpEvent.PickupXp});</li>
 *     <li>any orb that still tries to enter a level, including orbs loaded from an old save
 *         ({@link EntityJoinLevelEvent});</li>
 *     <li>anything that writes the experience fields behind the mod's back, covered by the tick
 *         fallback in {@link #onPlayerTick(PlayerTickEvent.Post)}.</li>
 * </ul>
 */
@EventBusSubscriber(modid = NoExperience.MOD_ID)
public final class ExperienceLock {
    private ExperienceLock() {}

    /** Experience orbs never drop, from any mob, including the ender dragon and the wither. */
    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        event.setDroppedExperience(0);
        event.setCanceled(true);
    }

    /** Blocks every "give this player experience points" call. */
    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        if (event.getAmount() > 0) {
            event.setCanceled(true);
        }
    }

    /** Blocks every "give this player experience levels" call. */
    @SubscribeEvent
    public static void onLevelChange(PlayerXpEvent.LevelChange event) {
        if (event.getLevels() > 0) {
            event.setCanceled(true);
        }
    }

    /** Orbs are consumed on contact but never pay out; the orb itself is removed as well. */
    @SubscribeEvent
    public static void onPickupXp(PlayerXpEvent.PickupXp event) {
        event.setCanceled(true);
        event.getOrb().discard();
    }

    /** No experience orb may enter a level, so already saved orbs disappear on chunk load too. */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ExperienceOrb) {
            event.setCanceled(true);
        }
    }

    /** Fallback lock: whatever wrote to the experience fields, it is zero again by the end of the tick. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            lockToZero(serverPlayer);
        }
    }

    /**
     * Resets the experience of {@code player} to zero and, when the value actually changed, tells
     * the client so its HUD and any client side display agree with the server.
     */
    public static void lockToZero(ServerPlayer player) {
        if (player.experienceLevel == 0 && player.experienceProgress == 0.0F && player.totalExperience == 0) {
            return;
        }
        zero(player);
        player.connection.send(new ClientboundSetExperiencePacket(0.0F, 0, 0));
    }

    private static void zero(Player player) {
        player.experienceLevel = 0;
        player.experienceProgress = 0.0F;
        player.totalExperience = 0;
    }
}
