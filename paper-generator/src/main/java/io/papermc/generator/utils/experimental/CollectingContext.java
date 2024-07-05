package io.papermc.generator.utils.experimental;

import com.mojang.serialization.Lifecycle;
import io.papermc.generator.Main;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public record CollectingContext<T>(Set<ResourceKey<T>> registered,
                                   Registry<T> registry) implements BootstrapContext<T> {

    @Override
    public Holder.Reference<T> register(final ResourceKey<T> key, final @NonNull T value, final Lifecycle lifecycle) {
        this.registered.add(key);
        return Holder.Reference.createStandAlone(this.registry.holderOwner(), key);
    }

    @Override
    public <S> HolderGetter<S> lookup(final ResourceKey<? extends Registry<? extends S>> key) {
        return Main.REGISTRY_ACCESS.lookupOrThrow(key);
    }
}
