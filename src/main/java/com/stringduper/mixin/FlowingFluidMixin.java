package com.stringduper.mixin;

import com.stringduper.StringDuperMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    /**
     * Water flows from a waterlogged trapdoor into an attached tripwire:
     * cancel the flow (the string survives) and drop 1-3 string instead.
     * Same rule as the StringDupersReturn plugin.
     */
    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    private void stringduper$spreadTo(LevelAccessor level, BlockPos pos, BlockState state,
                                      Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (!state.is(Blocks.TRIPWIRE)) return;
        if (!state.getValue(BlockStateProperties.ATTACHED)) return;

        BlockPos from = pos.relative(direction.getOpposite());
        BlockState fromState = level.getBlockState(from);
        if (!(fromState.getBlock() instanceof TrapDoorBlock)) return;
        if (!fromState.getValue(BlockStateProperties.WATERLOGGED)) return;

        ci.cancel();

        if (level instanceof ServerLevel serverLevel) {
            int min = StringDuperMod.minStringDrop;
            int max = StringDuperMod.maxStringDrop;
            if (min == 0 && max == 0) return;
            int count = min + serverLevel.getRandom().nextInt(max - min + 1);
            if (count > 0) {
                Block.popResource(serverLevel, pos, new ItemStack(Items.STRING, count));
            }
        }
    }
}
