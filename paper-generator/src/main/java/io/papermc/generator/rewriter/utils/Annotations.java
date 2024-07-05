package io.papermc.generator.rewriter.utils;

import io.papermc.generator.utils.experimental.SingleFlagHolder;
import io.papermc.typewriter.context.ImportCollector;
import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.utils.ClassHelper;
import java.lang.annotation.Annotation;
import org.bukkit.MinecraftExperimental;
import org.jetbrains.annotations.ApiStatus;

public final class Annotations {

    public static String annotation(Class<? extends Annotation> clazz, ImportCollector collector) {
        return "@%s".formatted(collector.getShortName(clazz));
    }

    public static String annotationStyle(Class<? extends Annotation> clazz) {
        return "@%s".formatted(ClassHelper.retrieveFullNestedName(clazz));
    }

    public static String annotation(Class<? extends Annotation> clazz, ImportCollector collector, String param, String value) {
        String annotation = annotation(clazz, collector);
        if (value.isEmpty()) {
            return annotation;
        }
        return "%s(%s = %s)".formatted(annotation, param, value);
    }

    public static String annotation(Class<? extends Annotation> clazz, ImportCollector collector, String value) {
        String annotation = annotation(clazz, collector);
        if (value.isEmpty()) {
            return annotation;
        }
        return "%s(%s)".formatted(annotation, value);
    }

    public static void experimentalAnnotations(final StringBuilder builder, final SearchMetadata metadata, final SingleFlagHolder requiredFeature) {
        builder.append(metadata.indent()).append(annotation(MinecraftExperimental.class, metadata.importCollector(), "%s.%s".formatted(
            metadata.importCollector().getShortName(MinecraftExperimental.Requires.class), requiredFeature.asAnnotationMember().name()
        ))).append('\n');

        builder.append(metadata.indent()).append(annotation(ApiStatus.Experimental.class, metadata.importCollector())).append('\n');
    }

    private Annotations() {
    }
}
