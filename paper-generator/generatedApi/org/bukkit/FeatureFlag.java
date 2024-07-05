package org.bukkit;

// Paper start - overhaul FeatureFlag API
import com.google.common.base.Preconditions;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.Index;
import org.intellij.lang.annotations.Subst;
// Paper end - overhaul FeatureFlag API
import org.jetbrains.annotations.ApiStatus;

/**
 * This represents a Feature Flag for a {@link io.papermc.paper.world.flag.FeatureFlagSetHolder}.
 */
public interface FeatureFlag extends Keyed {

    // Paper start - overhaul FeatureFlag API
    // Paper start - Generated/FeatureFlag
    // @GeneratedFrom 1.21.1
    @ApiStatus.Experimental
    FeatureFlag BUNDLE = create("bundle");

    @ApiStatus.Experimental
    FeatureFlag TRADE_REBALANCE = create("trade_rebalance");

    FeatureFlag VANILLA = create("vanilla");
    // Paper end - Generated/FeatureFlag

    @Deprecated(since = "1.20")
    FeatureFlag UPDATE_1_20 = deprecated("update_1_20");

    @Deprecated(since = "1.21")
    FeatureFlag UPDATE_121 = deprecated("update_1_21");

    /**
     * An index of all feature flags.
     */
    Index<Key, FeatureFlag> ALL_FLAGS = Index.create(FeatureFlag::key, List.copyOf(FeatureFlagImpl.ALL_FLAGS));

    private static FeatureFlag create(@Subst("vanilla") final String name) {
        final FeatureFlag flag = new FeatureFlagImpl(NamespacedKey.minecraft(name));
        Preconditions.checkState(FeatureFlagImpl.ALL_FLAGS.add(flag), "Tried to add duplicate feature flag: " + name);
        return flag;
    }

    private static FeatureFlag deprecated(@Subst("vanilla") final String name) {
        return new FeatureFlagImpl.Deprecated(NamespacedKey.minecraft(name));
    }
    // Paper end - overhaul FeatureFlag API
}
