package io.papermc.generator.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntityMapping {

    // if this become painful/too weird like the blockdata just rename the impl directly again
    private static final Map<String, String> RENAMES = ImmutableMap.<String, String>builder()
        .put("CraftFurnace", "CraftFurnaceFurnace")
        .put("CraftMobSpawner", "CraftCreatureSpawner")
        .put("CraftPiston", "CraftMovingPiston")
        .put("CraftTrappedChest", "CraftChest") // not really a rename
        .build();

    public static final Map<ResourceKey<BlockEntityType<?>>, String> MAPPING;

    static {
        Map<ResourceKey<BlockEntityType<?>>, String> map = new IdentityHashMap<>();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.registryKeySet().forEach(key -> {
            String name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, key.location().getPath());
            String implName = "Craft".concat(name);

            map.put(key, RENAMES.getOrDefault(implName, implName));
        });

        MAPPING = Collections.unmodifiableMap(map);
    }
}