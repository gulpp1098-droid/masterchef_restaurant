package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class RugUpdateStateQueueProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		boolean North = false;
		boolean South = false;
		boolean East = false;
		boolean West = false;
		double amountRugs = 0;
		double NBT = 0;
		amountRugs = 0;
		NBT = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "queue");
		for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
			if (directioniterator == Direction.NORTH && MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()
					&& (getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT + 1
							|| getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT - 1)) {
				amountRugs = amountRugs + 1;
				North = true;
			} else if (directioniterator == Direction.SOUTH && MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()
					&& (getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT + 1
							|| getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT - 1)) {
				amountRugs = amountRugs + 1;
				South = true;
			} else if (directioniterator == Direction.WEST && MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()
					&& (getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT + 1
							|| getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT - 1)) {
				amountRugs = amountRugs + 1;
				West = true;
			} else if (directioniterator == Direction.EAST && MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()
					&& (getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT + 1
							|| getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBT - 1)) {
				amountRugs = amountRugs + 1;
				East = true;
			}
		}
		if (amountRugs == 2 && (North && South || East && West)) {
			if (North) {
				setDirectionBlockState(world, x, y, z, Direction.WEST);
			} else if (West) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
			}
			setIntegerBlockState(world, x, y, z, "state", 2);
		} else if (amountRugs == 2) {
			if (North && East) {
				setDirectionBlockState(world, x, y, z, Direction.SOUTH);
			} else if (North && West) {
				setDirectionBlockState(world, x, y, z, Direction.EAST);
			} else if (West && South) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
			} else {
				setDirectionBlockState(world, x, y, z, Direction.WEST);
			}
			setIntegerBlockState(world, x, y, z, "state", 3);
		} else if (amountRugs == 1) {
			if (East) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
			} else if (West) {
				setDirectionBlockState(world, x, y, z, Direction.SOUTH);
			} else if (South) {
				setDirectionBlockState(world, x, y, z, Direction.EAST);
			} else {
				setDirectionBlockState(world, x, y, z, Direction.WEST);
			}
			setIntegerBlockState(world, x, y, z, "state", 1);
		} else {
			setIntegerBlockState(world, x, y, z, "state", 0);
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}

	private static void setDirectionBlockState(LevelAccessor world, double x, double y, double z, Direction value) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = world.getBlockState(pos);
		Property<?> property = state.getBlock().getStateDefinition().getProperty("facing");
		if (property instanceof DirectionProperty directionProperty && directionProperty.getPossibleValues().contains(value)) {
			world.setBlock(pos, state.setValue(directionProperty, value), 3);
		} else {
			property = state.getBlock().getStateDefinition().getProperty("axis");
			if (property instanceof EnumProperty enumProperty && enumProperty.getPossibleValues().contains(value.getAxis())) {
				world.setBlock(pos, state.setValue(enumProperty, value.getAxis()), 3);
			}
		}
	}

	private static void setIntegerBlockState(LevelAccessor world, double x, double y, double z, String property, int value) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = world.getBlockState(pos);
		if (state.getBlock().getStateDefinition().getProperty(property) instanceof IntegerProperty integerProperty && integerProperty.getPossibleValues().contains(value)) {
			world.setBlock(pos, state.setValue(integerProperty, value), 3);
		}
	}
}