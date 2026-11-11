package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.init.OmnichefModBlocks;

public class TradeBoardDestroyedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		double part = 0;
		double sideX = 0;
		double sideZ = 0;
		double mainX = 0;
		double mainZ = 0;
		double mainY = 0;
		if (!world.isClientSide()) {
			part = getIntFromBlockState(blockstate, "part");
			sideX = ((getDirectionFromBlockState(blockstate)).getCounterClockWise(Direction.Axis.Y)).getStepX();
			sideZ = ((getDirectionFromBlockState(blockstate)).getCounterClockWise(Direction.Axis.Y)).getStepZ();
			mainX = x;
			mainY = y;
			mainZ = z;
			if (part == 1 || part == 3) {
				mainX = mainX - sideX;
				mainZ = mainZ - sideZ;
			}
			if (part >= 2) {
				mainY = mainY - 1;
			}
			if (!(part == 0) && (world.getBlockState(BlockPos.containing(mainX, mainY, mainZ))).getBlock() == OmnichefModBlocks.TRADE_BOARD.get() && getIntFromBlockState((world.getBlockState(BlockPos.containing(mainX, mainY, mainZ))), "part") == 0
					&& (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(mainX, mainY, mainZ))))) == (getDirectionFromBlockState(blockstate))) {
				world.setBlock(BlockPos.containing(mainX, mainY, mainZ), Blocks.AIR.defaultBlockState(), 3);
			}
			if (!(part == 1) && (world.getBlockState(BlockPos.containing(mainX + sideX, mainY, mainZ + sideZ))).getBlock() == OmnichefModBlocks.TRADE_BOARD.get()
					&& getIntFromBlockState((world.getBlockState(BlockPos.containing(mainX + sideX, mainY, mainZ + sideZ))), "part") == 1
					&& (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(mainX + sideX, mainY, mainZ + sideZ))))) == (getDirectionFromBlockState(blockstate))) {
				world.setBlock(BlockPos.containing(mainX + sideX, mainY, mainZ + sideZ), Blocks.AIR.defaultBlockState(), 3);
			}
			if (!(part == 2) && (world.getBlockState(BlockPos.containing(mainX, mainY + 1, mainZ))).getBlock() == OmnichefModBlocks.TRADE_BOARD.get()
					&& getIntFromBlockState((world.getBlockState(BlockPos.containing(mainX, mainY + 1, mainZ))), "part") == 2
					&& (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(mainX, mainY + 1, mainZ))))) == (getDirectionFromBlockState(blockstate))) {
				world.setBlock(BlockPos.containing(mainX, mainY + 1, mainZ), Blocks.AIR.defaultBlockState(), 3);
			}
			if (!(part == 3) && (world.getBlockState(BlockPos.containing(mainX + sideX, mainY + 1, mainZ + sideZ))).getBlock() == OmnichefModBlocks.TRADE_BOARD.get()
					&& getIntFromBlockState((world.getBlockState(BlockPos.containing(mainX + sideX, mainY + 1, mainZ + sideZ))), "part") == 3
					&& (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(mainX + sideX, mainY + 1, mainZ + sideZ))))) == (getDirectionFromBlockState(blockstate))) {
				world.setBlock(BlockPos.containing(mainX + sideX, mainY + 1, mainZ + sideZ), Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	private static int getIntFromBlockState(BlockState blockState, String property) {
		Property<?> prop = blockState.getBlock().getStateDefinition().getProperty(property);
		return prop instanceof IntegerProperty ip ? blockState.getValue(ip) : -1;
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