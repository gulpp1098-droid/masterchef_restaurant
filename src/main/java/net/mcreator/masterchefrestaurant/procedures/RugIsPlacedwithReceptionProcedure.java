package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class RugIsPlacedwithReceptionProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Direction direction = Direction.NORTH;
		double X = 0;
		double Y = 0;
		double Z = 0;
		if (!world.isClientSide()) {
			direction = getBlockDirection(world, BlockPos.containing(x, y, z));
			Y = y;
			X = x + direction.getStepX();
			Z = z + direction.getStepZ();
			if (world.getBlockState(BlockPos.containing(X, Y - 1, Z)).canOcclude() && world.isEmptyBlock(BlockPos.containing(X, Y, Z))) {
				world.setBlock(BlockPos.containing(X, Y, Z), MasterchefRestaurantModBlocks.RUG_QUEUE.get().defaultBlockState(), 3);
				setBlockNBTNumber(world, X, Y, Z, "queue", 0);
				setBlockNBTNumber(world, x, y, z, "queue", 0);
				setBlockNBTNumber(world, X, Y, Z, "RestaurantID", (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID")));
				setBlockNBTText(world, x, y, z, "last_rug", ((X + 0.5) + ":" + y + ":" + (Z + 0.5)));
			} else {
				{
					BlockPos _pos = BlockPos.containing(x, y, z);
					Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
					world.destroyBlock(_pos, false);
				}
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("No valid position for Receptions Queue!"), false);
			}
		}
	}

	private static Direction getBlockDirection(LevelAccessor world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Property<?> property = blockState.getBlock().getStateDefinition().getProperty("facing");
		if (property != null && blockState.getValue(property) instanceof Direction direction)
			return direction;
		else if (blockState.hasProperty(BlockStateProperties.AXIS))
			return Direction.fromAxisAndDirection(blockState.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
		else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
			return Direction.fromAxisAndDirection(blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
		return Direction.NORTH;
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

	private static void setBlockNBTText(LevelAccessor world, double x, double y, double z, String tag, String value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putString(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}
}