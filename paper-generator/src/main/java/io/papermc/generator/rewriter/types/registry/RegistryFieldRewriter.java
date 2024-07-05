package io.papermc.generator.rewriter.types.registry;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import io.papermc.generator.Main;
import io.papermc.generator.rewriter.utils.Annotations;
import io.papermc.generator.utils.Formatting;
import io.papermc.generator.utils.RegistryUtils;
import io.papermc.generator.utils.experimental.FlagHolders;
import io.papermc.generator.utils.experimental.SingleFlagHolder;
import io.papermc.typewriter.ClassNamed;
import io.papermc.typewriter.SourceFile;
import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlags;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import static io.papermc.typewriter.utils.Formatting.quoted;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class RegistryFieldRewriter<T> extends SearchReplaceRewriter {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Registry<T> registry;
    private final Supplier<Set<ResourceKey<T>>> experimentalKeys;
    private final boolean isFilteredRegistry;
    private final @Nullable String fetchMethod;

    protected @MonotonicNonNull ClassNamed fieldClass;

    public RegistryFieldRewriter(final ResourceKey<? extends Registry<T>> registryKey, final @Nullable String fetchMethod) {
        this.registry = Main.REGISTRY_ACCESS.registryOrThrow(registryKey);
        this.experimentalKeys = Suppliers.memoize(() -> RegistryUtils.collectExperimentalDataDrivenKeys(this.registry));
        this.isFilteredRegistry = FeatureElement.FILTERED_REGISTRIES.contains(registryKey);
        this.fetchMethod = fetchMethod;
    }

    @Override
    public boolean registerFor(SourceFile file) {
        this.fieldClass = this.options.targetClass().orElse(file.mainClass());
        Preconditions.checkState(this.fieldClass.knownClass() != null, "This rewriter can't run without knowing the field class at runtime!");

        if (this.fetchMethod != null) {
            try {
                this.fieldClass.knownClass().getDeclaredMethod(this.fetchMethod, String.class);
            } catch (NoSuchMethodException e) {
                LOGGER.error("Fetch method not found, skipping the rewriter for registry fields of {}", this.registry.key(), e);
                return false;
            }
        }

        return super.registerFor(file);
    }

    @Override
    protected void insert(final SearchMetadata metadata, final StringBuilder builder) {
        boolean isInterface = this.fieldClass.knownClass() != null && this.fieldClass.knownClass().isInterface();
        Iterator<Holder.Reference<T>> referenceIterator = this.registry.holders().filter(this::canPrintField).sorted(Formatting.alphabeticKeyOrder(reference -> reference.key().location().getPath())).iterator();

        while (referenceIterator.hasNext()) {
            Holder.Reference<T> reference = referenceIterator.next();

            this.rewriteJavadocs(reference, metadata.indent(), builder);

            @Nullable SingleFlagHolder requiredFeature = this.getRequiredFeature(reference);
            if (requiredFeature != null) {
                Annotations.experimentalAnnotations(builder, metadata, requiredFeature);
            }

            builder.append(metadata.indent());
            if (!isInterface) {
                builder.append("%s %s %s ".formatted(PUBLIC, STATIC, FINAL));
            }

            builder.append(this.rewriteFieldType(reference)).append(' ').append(this.rewriteFieldName(reference));
            builder.append(" = ");
            builder.append(this.rewriteFieldValue(reference));
            builder.append(';');

            builder.append('\n');
            if (referenceIterator.hasNext()) {
                builder.append('\n');
            }
        }
    }

    protected void rewriteJavadocs(Holder.Reference<T> reference, String indent, StringBuilder builder) {
    }

    protected boolean canPrintField(Holder.Reference<T> reference) {
        return true;
    }

    protected String rewriteFieldType(Holder.Reference<T> reference) {
        return this.fieldClass.simpleName();
    }

    protected String rewriteFieldName(Holder.Reference<T> reference) {
        return Formatting.formatKeyAsField(reference.key().location().getPath());
    }

    protected String rewriteFieldValue(Holder.Reference<T> reference) {
        return "%s(%s)".formatted(this.fetchMethod, quoted(reference.key().location().getPath()));
    }

    protected @Nullable SingleFlagHolder getRequiredFeature(Holder.Reference<T> reference) {
        if (this.isFilteredRegistry) {
            // built-in registry
            FeatureElement element = (FeatureElement) reference.value();
            if (FeatureFlags.isExperimental(element.requiredFeatures())) {
                return SingleFlagHolder.fromSet(element.requiredFeatures());
            }
        } else {
            // data-driven registry
            if (this.experimentalKeys.get().contains(reference.key())) {
                return FlagHolders.NEXT_UPDATE;
            }
        }
        return null;
    }
}
