package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class TradeBoardPlacedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		double sideX = 0;
		double sideZ = 0;
		if (!world.isClientSide()) {
			sideX = ((getDirectionFromBlockState(blockstate)).getCounterClockWise(Direction.Axis.Y)).getStepX();
			sideZ = ((getDirectionFromBlockState(blockstate)).getCounterClockWise(Direction.Axis.Y)).getStepZ();
			world.setBlock(BlockPos.containing(x + sideX, y, z + sideZ), (blockStateWithInt((world.getBlockState(BlockPos.containing(x, y, z))), "part", 1)), 3);
			world.setBlock(BlockPos.containing(x, y + 1, z), (blockStateWithInt((world.getBlockState(BlockPos.containing(x, y, z))), "part", 2)), 3);
			world.setBlock(BlockPos.containing(x + sideX, y + 1, z + sideZ), (blockStateWithInt((world.getBlockState(BlockPos.containing(x, y, z))), "part", 3)), 3);
		}
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static BlockState blockStateWithInt(BlockState blockState, String property, int newValue) {
		Property<?> prop = blockState.getBlock().getStateDefinition().getProperty(property);
		return prop instanceof IntegerProperty ip && prop.getPossibleValues().contains(newValue) ? blockState.setValue(ip, newValue) : blockState;
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