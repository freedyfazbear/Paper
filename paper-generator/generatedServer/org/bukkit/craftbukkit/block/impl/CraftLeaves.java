package org.bukkit.craftbukkit.block.impl;

import io.papermc.paper.generated.GeneratedFrom;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

@GeneratedFrom("1.21.1")
public class CraftLeaves extends CraftBlockData implements Leaves {
    private static final IntegerProperty DISTANCE = LeavesBlock.DISTANCE;

    private static final BooleanProperty PERSISTENT = LeavesBlock.PERSISTENT;

    private static final BooleanProperty WATERLOGGED = LeavesBlock.WATERLOGGED;

    public CraftLeaves(BlockState state) {
        super(state);
    }

    @Override
    public int getDistance() {
        return this.get(DISTANCE);
    }

    @Override
    public void setDistance(final int distance) {
        this.set(DISTANCE, distance);
    }

    @Override
    public int getMinimumDistance() {
        return DISTANCE.min;
    }

    @Override
    public int getMaximumDistance() {
        return DISTANCE.max;
    }

    @Override
    public boolean isPersistent() {
        return this.get(PERSISTENT);
    }

    @Override
    public void setPersistent(final boolean persistent) {
        this.set(PERSISTENT, persistent);
    }

    @Override
    public boolean isWaterlogged() {
        return this.get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(final boolean waterlogged) {
        this.set(WATERLOGGED, waterlogged);
    }
}