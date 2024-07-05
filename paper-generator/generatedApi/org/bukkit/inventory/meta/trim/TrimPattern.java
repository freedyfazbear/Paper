package org.bukkit.inventory.meta.trim;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Translatable;

/**
 * Represents a pattern that may be used in an {@link ArmorTrim}.
 */
public interface TrimPattern extends Keyed, Translatable {

    // Paper start - Generated/TrimPattern
    // @GeneratedFrom 1.21.1
    TrimPattern BOLT = getTrimPattern("bolt");

    TrimPattern COAST = getTrimPattern("coast");

    TrimPattern DUNE = getTrimPattern("dune");

    TrimPattern EYE = getTrimPattern("eye");

    TrimPattern FLOW = getTrimPattern("flow");

    TrimPattern HOST = getTrimPattern("host");

    TrimPattern RAISER = getTrimPattern("raiser");

    TrimPattern RIB = getTrimPattern("rib");

    TrimPattern SENTRY = getTrimPattern("sentry");

    TrimPattern SHAPER = getTrimPattern("shaper");

    TrimPattern SILENCE = getTrimPattern("silence");

    TrimPattern SNOUT = getTrimPattern("snout");

    TrimPattern SPIRE = getTrimPattern("spire");

    TrimPattern TIDE = getTrimPattern("tide");

    TrimPattern VEX = getTrimPattern("vex");

    TrimPattern WARD = getTrimPattern("ward");

    TrimPattern WAYFINDER = getTrimPattern("wayfinder");

    TrimPattern WILD = getTrimPattern("wild");
    // Paper end - Generated/TrimPattern

    private static TrimPattern getTrimPattern(String name) {
        NamespacedKey key = NamespacedKey.minecraft(name);
        return io.papermc.paper.registry.RegistryAccess.registryAccess().getRegistry(io.papermc.paper.registry.RegistryKey.TRIM_PATTERN).get(key);
    }

    // Paper start - adventure
    /**
     * Get the description of this {@link TrimPattern}.
     *
     * @return the description
     */
    net.kyori.adventure.text.@org.jetbrains.annotations.NotNull Component description();

    /**
     * @deprecated this method assumes that {@link #description()} will
     * always be a translatable component which is not guaranteed.
     */
    @Override
    @Deprecated(forRemoval = true)
    @org.jetbrains.annotations.NotNull String getTranslationKey();
    // Paper end - adventure

    // Paper start - Registry#getKey
    /**
     * @deprecated use {@link Registry#getKey(Keyed)} and {@link Registry#TRIM_PATTERN}. TrimPatterns
     * can exist without a key.
     */
    @Deprecated(forRemoval = true, since = "1.20.4")
    @Override
    org.bukkit.@org.jetbrains.annotations.NotNull NamespacedKey getKey();
    // Paper end - Registry#getKey
}
