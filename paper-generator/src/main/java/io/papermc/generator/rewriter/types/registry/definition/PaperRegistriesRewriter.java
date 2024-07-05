package io.papermc.generator.rewriter.types.registry.definition;

import io.papermc.generator.rewriter.types.Types;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import io.papermc.typewriter.utils.ClassHelper;
import net.minecraft.core.registries.Registries;
import org.bukkit.Registry;

public class PaperRegistriesRewriter extends SearchReplaceRewriter {

    private void appendEntry(String indent, StringBuilder builder, RegistryEntry<?> entry, boolean canBeDelayed, boolean apiOnly) {
        builder.append(indent);
        if (apiOnly) {
            builder.append("apiOnly");
        } else {
            if (entry.apiRegistryBuilderImpl() != null) {
                builder.append("writable");
            } else {
                builder.append("entry");
            }
        }
        builder.append('(');
        builder.append(Registries.class.getSimpleName()).append('.').append(entry.registryKeyField());
        builder.append(", ");
        builder.append(RegistryKey.class.getSimpleName()).append('.').append(entry.registryKeyField());
        builder.append(", ");
        if (apiOnly) {
            builder.append("() -> ");
            builder.append(Registry.class.getCanonicalName()).append('.').append(entry.apiRegistryField().orElse(entry.registryKeyField()));
        } else {
            String apiClassName = ClassHelper.retrieveFullNestedName(entry.apiClass());
            builder.append(apiClassName).append(".class");
            builder.append(", ");

            builder.append(entry.implClass()).append("::new");
            if (entry.apiRegistryBuilderImpl() != null) {
                builder.append(", ");
                builder.append(entry.apiRegistryBuilderImpl()).append("::new");
            }
        }
        builder.append(')');
        if (entry.fieldRename() != null) {
            builder.append(".withSerializationUpdater(").append(Types.FIELD_RENAME.simpleName()).append('.').append(entry.fieldRename()).append(")");
        }
        if (canBeDelayed && entry.isDelayed()) {
            builder.append(".delayed()");
        }
        builder.append(',');
        builder.append('\n');
    }

    @Override
    public void insert(SearchMetadata metadata, StringBuilder builder) {
        builder.append(metadata.indent()).append("// built-in");
        builder.append('\n');

        for (RegistryEntry<?> entry : RegistryEntries.BUILT_IN) {
            appendEntry(metadata.indent(), builder, entry, false, false);
        }

        builder.append('\n');
        builder.append(metadata.indent()).append("// data-driven");
        builder.append('\n');

        for (RegistryEntry<?> entry : RegistryEntries.DATA_DRIVEN) {
            appendEntry(metadata.indent(), builder, entry, true, false);
        }

        builder.append('\n');
        builder.append(metadata.indent()).append("// api-only");
        builder.append('\n');

        for (RegistryEntry<?> entry : RegistryEntries.API_ONLY) {
            appendEntry(metadata.indent(), builder, entry, false, true);
        }

        builder.deleteCharAt(builder.length() - 2); // delete extra comma...
    }
}
