package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.init.OmnichefModBlocks;

public class IsQueueRugForRestaurantProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, double restaurantID) {
		if (!((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == OmnichefModBlocks.RUG_QUEUE.get())) {
			return false;
		} else {
			if (!(getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID") == restaurantID)) {
				return false;
			}
		}
		return true;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}