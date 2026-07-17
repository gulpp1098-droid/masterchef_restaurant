package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;
import net.mcreator.masterchefrestaurant.block.ChairBlock;

import java.util.ArrayList;

public class ServiceTableNeighbourBlockChangesProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		BlockState ChairBlock = Blocks.AIR.defaultBlockState();
		ArrayList<Object> directionsArray = new ArrayList<>();
		double ChairAmount = 0;
		double numberSet = 0;
		double indexSet = 0;
		Direction direction = Direction.NORTH;
		ChairAmount = 0;
		for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
			ChairBlock = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())));
			if (ChairBlock.getBlock() == MasterchefRestaurantModBlocks.CHAIR.get() && (getDirectionFromBlockState(ChairBlock)) == (directioniterator.getOpposite())) {
				ChairAmount = ChairAmount + 1;
				directionsArray.add(directioniterator);
			}
		}
		setBlockNBTNumber(world, x, y, z, "ChairAmount", ChairAmount);
		indexSet = 0;
		while (directionsArray.size() > 0) {
			numberSet = Mth.nextInt(RandomSource.create(), 0, (int) (directionsArray.size() - 1));
			direction = directionsArray.get((int) numberSet) instanceof Direction _direction17 ? _direction17 : Direction.UP;
			setBlockNBTNumber(world, (x + direction.getStepX()), y, (z + direction.getStepZ()), "ChairNumber", indexSet);
			setBlockNBTNumber(world, (x + direction.getStepX()), y, (z + direction.getStepZ()), "TableNumber", (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "TableNumber")));
			setBlockNBTNumber(world, (x + direction.getStepX()), y, (z + direction.getStepZ()), "RestaurantID", (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID")));
			directionsArray.remove((int) numberSet);
			indexSet = indexSet + 1;
		}
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static void setBlockNBTNumber(LevelAccessor world, double x, double y, double z, String tag, double value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putDouble(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
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