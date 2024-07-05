package io.papermc.generator.rewriter.types.simple;

import io.papermc.generator.rewriter.types.registry.RegistryFieldRewriter;
import io.papermc.generator.utils.experimental.FlagHolders;
import io.papermc.generator.utils.experimental.SingleFlagHolder;
import io.papermc.typewriter.utils.ClassHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated // bad generic
public class ItemTypeRewriter extends RegistryFieldRewriter<Item> {

    public ItemTypeRewriter() {
        super(Registries.ITEM, "getItemType");
    }

    @Override
    protected String rewriteFieldType(Holder.Reference<Item> reference) {
        if (reference.value().equals(Items.AIR)) {
            return super.rewriteFieldType(reference);
        }

        return "%s<%s>".formatted(ClassHelper.retrieveFullNestedName(ItemType.Typed.class), ItemMeta.class.getSimpleName());
    }

    @Override
    protected @Nullable SingleFlagHolder getRequiredFeature(Holder.Reference<Item> reference) {
        if (reference.value() instanceof BundleItem) {
            return FlagHolders.BUNDLE; // special case since the item is not locked itself just in the creative menu
        } else {
            return super.getRequiredFeature(reference);
        }
    }
}
