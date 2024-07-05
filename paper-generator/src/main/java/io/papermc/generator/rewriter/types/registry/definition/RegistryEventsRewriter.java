package io.papermc.generator.rewriter.types.registry.definition;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.event.RegistryEventProvider;
import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import io.papermc.typewriter.utils.ClassHelper;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class RegistryEventsRewriter extends SearchReplaceRewriter {

    @Override
    public void insert(SearchMetadata metadata, StringBuilder builder) {
        RegistryEntries.forEach(entry -> {
            if (entry.apiRegistryBuilder() != null) {
                builder.append(metadata.indent());
                builder.append("%s %s %s ".formatted(PUBLIC, STATIC, FINAL));
                builder.append(RegistryEventProvider.class.getSimpleName());
                builder.append("<").append(entry.apiClass().getSimpleName()).append(", ").append(ClassHelper.retrieveFullNestedName(entry.apiRegistryBuilder())).append('>');
                builder.append(' ');
                builder.append(entry.registryKeyField());
                builder.append(" = ");
                builder.append("create(").append(RegistryKey.class.getSimpleName()).append('.').append(entry.registryKeyField()).append(");");
                builder.append('\n');
            }
        });
    }
}
