package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.init.OmnichefModBlocks;

public class TradeBoardCanPlaceProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		double sideX = 0;
		double sideZ = 0;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == OmnichefModBlocks.TRADE_BOARD.get()) {
			return true;
		}
		sideX = ((getDirectionFromBlockState(blockstate)).getCounterClockWise(Direction.Axis.Y)).getStepX();
		sideZ = ((getDirectionFromBlockState(blockstate)).getCounterClockWise(Direction.Axis.Y)).getStepZ();
		return world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)) && world.isEmptyBlock(BlockPos.containing(x + sideX, y, z + sideZ)) && world.isEmptyBlock(BlockPos.containing(x, y + 1, z))
				&& world.isEmptyBlock(BlockPos.containing(x + sideX, y + 1, z + sideZ));
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static Property<?> getPropertyByName(BlockState state, String name) {
		for (Property<?> property : state.getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}
}