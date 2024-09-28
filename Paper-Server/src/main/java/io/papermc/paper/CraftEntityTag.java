package io.papermc.paper;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tags;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.tag.CraftTag;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class CraftEntityTag extends CraftTag<EntityTypes<?>, EntityType> {

    public CraftEntityTag(Tags<EntityTypes<?>> registry, MinecraftKey tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(EntityType item) {
        return getHandle().isTagged(CraftMagicNumbers.getEntityTypes(item));
    }

    @Override
    public Set<EntityType> getValues() {
        return Collections.unmodifiableSet(getHandle().getTagged().stream().map(CraftMagicNumbers::getEntityType).collect(Collectors.toSet()));
    }
}
