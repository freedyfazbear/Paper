package io.papermc.generator.rewriter.types.registry;

import io.papermc.generator.Main;
import io.papermc.generator.rewriter.utils.Annotations;
import io.papermc.generator.utils.Formatting;
import io.papermc.generator.utils.TagRegistry;
import io.papermc.generator.utils.experimental.SingleFlagHolder;
import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import static io.papermc.typewriter.utils.Formatting.quoted;

public class TagRewriter extends SearchReplaceRewriter {

    @Override
    protected void insert(final SearchMetadata metadata, final StringBuilder builder) {
        for (int i = 0, len = TagRegistry.SUPPORTED_REGISTRIES.length; i < len; i++) {
            final TagRegistry tagRegistry = TagRegistry.SUPPORTED_REGISTRIES[i];

            final ResourceKey<? extends Registry<?>> registryKey = tagRegistry.registryKey();
            final Registry<?> registry = Main.REGISTRY_ACCESS.registryOrThrow(registryKey);

            final String fieldPrefix = Formatting.formatTagFieldPrefix(tagRegistry.folderName(), registryKey);
            final String registryFieldName = "REGISTRY_" + tagRegistry.folderName().toUpperCase(Locale.ENGLISH);

            if (i != 0) {
                builder.append('\n'); // extra line before the registry field
            }

            // registry name field
            builder.append(metadata.indent());
            builder.append("%s %s = %s;".formatted(String.class.getSimpleName(), registryFieldName, quoted(tagRegistry.folderName())));

            builder.append('\n');
            builder.append('\n');

            Iterator<? extends TagKey<?>> keyIterator = registry.getTagNames().sorted(Formatting.alphabeticKeyOrder(tagKey -> tagKey.location().getPath())).iterator();

            while (keyIterator.hasNext()) {
                TagKey<?> tagKey = keyIterator.next();
                final String keyPath = tagKey.location().getPath();
                final String fieldName = fieldPrefix + Formatting.formatKeyAsField(keyPath);

                // tag field
                String featureFlagName = Main.EXPERIMENTAL_TAGS.get(tagKey);
                if (featureFlagName != null) {
                    Annotations.experimentalAnnotations(builder, metadata, SingleFlagHolder.fromVanillaName(featureFlagName));
                }

                builder.append(metadata.indent());
                builder.append("%s<%s>".formatted(metadata.source().mainClass().simpleName(), tagRegistry.apiType().getSimpleName())).append(' ').append(fieldName);
                builder.append(" = ");
                builder.append("%s.getTag(%s, %s.minecraft(%s), %s.class)".formatted(Bukkit.class.getSimpleName(), registryFieldName, NamespacedKey.class.getSimpleName(), quoted(keyPath), tagRegistry.apiType().getSimpleName()));
                builder.append(';');

                builder.append('\n');
                if (keyIterator.hasNext()) {
                    builder.append('\n');
                }
            }
        }
    }
}
