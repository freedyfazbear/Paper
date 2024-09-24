package io.papermc.generator.utils;

import org.checkerframework.checker.nullness.qual.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

public final class ClassHelper {

    public static Set<Class<?>> getAllInterfaces(Class<?> clazz, Class<?> ignored, Set<Class<?>> interfaces) {
        Class<?>[] classes = clazz.getInterfaces();
        interfaces.addAll(Arrays.asList(classes));
        for (Class<?> farClass : classes) {
            if (farClass == ignored) {
                continue;
            }
            getAllInterfaces(farClass, ignored, interfaces);
        }
        interfaces.remove(ignored);
        return interfaces;
    }

    public static @Nullable Type getNestedTypeParameter(Type type, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (!(type instanceof ParameterizedType complexType)) {
                return null;
            }

            Type[] types = complexType.getActualTypeArguments();
            if (types.length != 1) {
                return null;
            }

            if (clazz == null || eraseType(types[0]) == clazz) {
                type = types[0];
            }
        }

        return type;
    }

    public static Class<?> eraseType(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        if (type instanceof ParameterizedType complexType) {
            return eraseType(complexType.getRawType());
        }
        throw new UnsupportedOperationException("Don't know how to turn " + type + " into its erased type!");
    }

    public static boolean isStaticConstant(Field field, int extraFlags) {
        int flags = extraFlags | Modifier.STATIC | Modifier.FINAL;
        return (field.getModifiers() & flags) == flags;
    }

    public static <T> Class<? extends T> classOr(String className, Class<? extends T> defaultClass) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return defaultClass;
        }
    }

    private ClassHelper() {
    }
}
