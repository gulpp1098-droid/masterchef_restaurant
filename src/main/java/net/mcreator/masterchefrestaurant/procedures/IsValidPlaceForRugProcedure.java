package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class IsValidPlaceForRugProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		if (world.getBlockState(BlockPos.containing(x, y - 1, z)).canOcclude()) {
			return true;
		} else if (!world.isClientSide() && (world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get() && !getBlockNBTLogic(world, BlockPos.containing(x, y, z), "cleanup_started")) {
			setBlockNBTLogic(world, x, y, z, "cleanup_started", true);
			CleanupRestaurantQueueFromRugProcedure.execute(world, x, y, z);
		}
		return false;
	}

	private static boolean getBlockNBTLogic(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getBoolean(tag);
		return false;
	}

	private static void setBlockNBTLogic(LevelAccessor world, double x, double y, double z, String tag, boolean value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putBoolean(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}
}