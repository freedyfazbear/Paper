package io.papermc.generator;

import io.papermc.generator.utils.BlockStateMapping;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class BlockStatePropertyTest {

    private static Set<Class<? extends Comparable<?>>> ENUM_PROPERTY_VALUES;

    @BeforeAll
    public static void getAllProperties() {
        // bootstrap
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Bootstrap.validate();

        // get all properties
        Set<Class<? extends Comparable<?>>> enumPropertyValues = Collections.newSetFromMap(new IdentityHashMap<>());
        try {
            for (Field field : BlockStateProperties.class.getDeclaredFields()) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) & Modifier.isFinal(mod) & Modifier.isPublic(mod)) {
                    if (!EnumProperty.class.isAssignableFrom(field.getType())) {
                        continue;
                    }

                    EnumProperty<?> property = ((EnumProperty<?>) field.get(null));
                    enumPropertyValues.add(property.getValueClass());
                }
            }
            ENUM_PROPERTY_VALUES = Collections.unmodifiableSet(enumPropertyValues);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    {
        // reference test
        // if renamed should change DataPropertyWriter#FIELD_TO_BASE_NAME
        var a = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES;
        var b = PipeBlock.PROPERTY_BY_DIRECTION;
    }

    @Test
    public void testBridge() {
        Set<String> missingApisEquivalent = new HashSet<>();
        for (Class<? extends Comparable<?>> value : ENUM_PROPERTY_VALUES) {
            if (!BlockStateMapping.ENUM_BRIDGE.containsKey(value)) {
                missingApisEquivalent.add(value.getCanonicalName());
            }
        }

        if (!missingApisEquivalent.isEmpty()) {
            fail("Missing some api equivalent in the blockstate mapping enum bridge (BlockStateMapping#ENUM_BRIDGE) : " + String.join(", ", missingApisEquivalent));
        }
    }
}
