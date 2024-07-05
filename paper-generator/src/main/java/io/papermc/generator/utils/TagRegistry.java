package io.papermc.generator.utils;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.bukkit.Fluid;
import org.bukkit.GameEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public record TagRegistry(String folderName, Class<?> apiType, ResourceKey<? extends Registry<?>> registryKey) {

    public static final TagRegistry[] SUPPORTED_REGISTRIES = { // 1.21 folder name are normalized to registry key but api will stay as is
        new TagRegistry("blocks", Material.class, Registries.BLOCK),
        new TagRegistry("items", Material.class, Registries.ITEM),
        new TagRegistry("fluids", Fluid.class, Registries.FLUID),
        new TagRegistry("entity_types", EntityType.class, Registries.ENTITY_TYPE),
        new TagRegistry("game_events", GameEvent.class, Registries.GAME_EVENT)
    };
}
