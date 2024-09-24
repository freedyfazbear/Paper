package io.papermc.generator.rewriter.types.registry.definition;

public record RegistryField<T>(Class<T> elementClass, String fieldName) {
}
