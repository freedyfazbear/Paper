package io.papermc.generator.rewriter.types.registry.definition;

import io.papermc.generator.Main;
import io.papermc.generator.utils.ClassHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class RegistryEntry<T> {

    private final ResourceKey<? extends Registry<T>> registryKey;
    private final Class<T> registryElementClass;
    private final @Nullable Class<?> registryConstantClass;
    private final String registryKeyField;

    private final Class<?> apiClass;
    private final String implClass;

    private @Nullable Class<?> apiRegistryBuilder;
    private @Nullable String apiRegistryBuilderImpl;

    private @Nullable String fieldRename;
    private boolean delayed;
    private Optional<String> apiRegistryField = Optional.empty();

    private @Nullable Map<ResourceKey<T>, String> fieldNames;

    public RegistryEntry(ResourceKey<? extends Registry<T>> registryKey, Class<T> registryElementClass, @Nullable Class<?> registryConstantClass, String registryKeyField, Class<?> apiClass, String implClass) {
        this.registryKey = registryKey;
        this.registryElementClass = registryElementClass;
        this.registryConstantClass = registryConstantClass;
        this.registryKeyField = registryKeyField;
        this.apiClass = apiClass;
        this.implClass = implClass;
    }

    public ResourceKey<? extends Registry<T>> registryKey() {
        return this.registryKey;
    }

    public Registry<T> registry() {
        return Main.REGISTRY_ACCESS.registryOrThrow(this.registryKey);
    }

    public String registryKeyField() {
        return this.registryKeyField;
    }

    public Class<?> apiClass() {
        return this.apiClass;
    }

    public String implClass() {
        return this.implClass;
    }

    public RegistryEntry<T> delayed() {
        this.delayed = true;
        return this;
    }

    public RegistryEntry<T> withSerializationUpdater(String fieldName) {
        this.fieldRename = fieldName;
        return this;
    }

    public boolean isDelayed() {
        return this.delayed;
    }

    public @Nullable String fieldRename() {
        return this.fieldRename;
    }

    public @Nullable Class<?> apiRegistryBuilder() {
        return this.apiRegistryBuilder;
    }

    public @Nullable String apiRegistryBuilderImpl() {
        return this.apiRegistryBuilderImpl;
    }

    public RegistryEntry<T> apiRegistryBuilder(Class<?> builderClass, String builderImplClass) {
        this.apiRegistryBuilder = builderClass;
        this.apiRegistryBuilderImpl = builderImplClass;
        return this;
    }

    public Optional<String> apiRegistryField() {
        return this.apiRegistryField;
    }

    public RegistryEntry<T> apiRegistryField(String registryField) {
        this.apiRegistryField = Optional.of(registryField);
        return this;
    }

    public String keyClassName() {
        if (RegistryEntries.REGISTRY_CLASS_NAME_BASED_ON_API.contains(this.apiClass)) {
            return this.apiClass.getSimpleName();
        }

        return this.registryElementClass.getSimpleName();
    }

    public boolean allowCustomKeys() {
        return this.apiRegistryBuilder != null || RegistryEntries.DATA_DRIVEN.contains(this);
    }

    private <TO> Map<ResourceKey<T>, TO> getFields(Map<ResourceKey<T>, TO> map, Function<Field, @Nullable TO> transform) {
        Registry<T> registry = this.registry();
        try {
            for (final Field field : this.registryConstantClass.getDeclaredFields()) {
                if (!ResourceKey.class.isAssignableFrom(field.getType()) && !Holder.Reference.class.isAssignableFrom(field.getType()) && !this.registryElementClass.isAssignableFrom(field.getType())) {
                    continue;
                }

                if (ClassHelper.isStaticConstant(field, Modifier.PUBLIC)) {
                    @Nullable ResourceKey<T> key = null;
                    if (this.registryElementClass.isAssignableFrom(field.getType())) {
                        key = registry.getResourceKey(this.registryElementClass.cast(field.get(null))).orElseThrow();
                    } else {
                        if (field.getGenericType() instanceof ParameterizedType complexType && complexType.getActualTypeArguments().length == 1 &&
                            complexType.getActualTypeArguments()[0] == this.registryElementClass) {

                            if (Holder.Reference.class.isAssignableFrom(field.getType())) {
                                key = ((Holder.Reference<T>) field.get(null)).key();
                            } else {
                                key = (ResourceKey<T>) field.get(null);
                            }
                        }
                    }
                    if (key != null) {
                        TO value = transform.apply(field);
                        if (value != null) {
                            map.put(key, value);
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
        return map;
    }

    public Map<ResourceKey<T>, String> getFieldNames() {
        if (this.fieldNames == null) {
            this.fieldNames = this.getFields(Field::getName);
        }
        return this.fieldNames;
    }

    public <TO> Map<ResourceKey<T>, TO> getFields(Function<Field, @Nullable TO> transform) {
        if (this.registryConstantClass == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(this.getFields(new IdentityHashMap<>(), transform));
    }

    @Override
    public String toString() {
        return "RegistryEntry[" +
            "registryKey=" + this.registryKey + ", " +
            "apiRegistryKey=" + this.registryKeyField + ", " +
            "apiClass=" + this.apiClass + ", " +
            "implClass=" + this.implClass + ", " +
            ']';
    }
}
