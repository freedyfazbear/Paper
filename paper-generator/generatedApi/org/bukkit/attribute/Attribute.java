package org.bukkit.attribute;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Translatable;
import org.jetbrains.annotations.NotNull;

/**
 * Types of attributes which may be present on an {@link Attributable}.
 */
public enum Attribute implements Keyed, Translatable, net.kyori.adventure.translation.Translatable { // Paper - Adventure translations

    // Paper start - Generated/Attribute
    // @GeneratedFrom 1.21.1
    GENERIC_ARMOR("generic.armor"),
    GENERIC_ARMOR_TOUGHNESS("generic.armor_toughness"),
    GENERIC_ATTACK_DAMAGE("generic.attack_damage"),
    GENERIC_ATTACK_KNOCKBACK("generic.attack_knockback"),
    GENERIC_ATTACK_SPEED("generic.attack_speed"),
    GENERIC_BURNING_TIME("generic.burning_time"),
    GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE("generic.explosion_knockback_resistance"),
    GENERIC_FALL_DAMAGE_MULTIPLIER("generic.fall_damage_multiplier"),
    GENERIC_FLYING_SPEED("generic.flying_speed"),
    GENERIC_FOLLOW_RANGE("generic.follow_range"),
    GENERIC_GRAVITY("generic.gravity"),
    GENERIC_JUMP_STRENGTH("generic.jump_strength"),
    GENERIC_KNOCKBACK_RESISTANCE("generic.knockback_resistance"),
    GENERIC_LUCK("generic.luck"),
    GENERIC_MAX_ABSORPTION("generic.max_absorption"),
    GENERIC_MAX_HEALTH("generic.max_health"),
    GENERIC_MOVEMENT_EFFICIENCY("generic.movement_efficiency"),
    GENERIC_MOVEMENT_SPEED("generic.movement_speed"),
    GENERIC_OXYGEN_BONUS("generic.oxygen_bonus"),
    GENERIC_SAFE_FALL_DISTANCE("generic.safe_fall_distance"),
    GENERIC_SCALE("generic.scale"),
    GENERIC_STEP_HEIGHT("generic.step_height"),
    GENERIC_WATER_MOVEMENT_EFFICIENCY("generic.water_movement_efficiency"),
    PLAYER_BLOCK_BREAK_SPEED("player.block_break_speed"),
    PLAYER_BLOCK_INTERACTION_RANGE("player.block_interaction_range"),
    PLAYER_ENTITY_INTERACTION_RANGE("player.entity_interaction_range"),
    PLAYER_MINING_EFFICIENCY("player.mining_efficiency"),
    PLAYER_SNEAKING_SPEED("player.sneaking_speed"),
    PLAYER_SUBMERGED_MINING_SPEED("player.submerged_mining_speed"),
    PLAYER_SWEEPING_DAMAGE_RATIO("player.sweeping_damage_ratio"),
    ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawn_reinforcements");
    // Paper end - Generated/Attribute

    private final NamespacedKey key;

    private Attribute(String key) {
        this.key = NamespacedKey.minecraft(key);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return Bukkit.getUnsafe().getTranslationKey(this);
    }

    // Paper start
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull String translationKey() {
        return Bukkit.getUnsafe().getTranslationKey(this);
    }
    // Paper end
}
