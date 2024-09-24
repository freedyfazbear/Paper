package org.bukkit.craftbukkit.block.impl;

import com.google.common.base.Preconditions;
import io.papermc.paper.generated.GeneratedFrom;
import java.util.Set;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

@GeneratedFrom("1.21.1")
public class CraftEndPortalFrame extends CraftBlockData implements EndPortalFrame {
    private static final BooleanProperty HAS_EYE = EndPortalFrameBlock.HAS_EYE;

    private static final DirectionProperty FACING = EndPortalFrameBlock.FACING;

    public CraftEndPortalFrame(BlockState state) {
        super(state);
    }

    @Override
    public boolean hasEye() {
        return this.get(HAS_EYE);
    }

    @Override
    public void setEye(final boolean eye) {
        this.set(HAS_EYE, eye);
    }

    @Override
    public BlockFace getFacing() {
        return this.get(FACING, BlockFace.class);
    }

    @Override
    public void setFacing(final BlockFace blockFace) {
        Preconditions.checkArgument(blockFace != null, "blockFace cannot be null!");
        Preconditions.checkArgument(blockFace.isCartesian() && blockFace.getModY() == 0, "Invalid face, only cartesian horizontal face are allowed for this property!");
        this.set(FACING, blockFace);
    }

    @Override
    public Set<BlockFace> getFaces() {
        return this.getValues(FACING, BlockFace.class);
    }
}