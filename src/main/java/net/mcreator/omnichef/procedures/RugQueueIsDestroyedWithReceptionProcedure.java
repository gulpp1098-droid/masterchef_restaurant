package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class RugQueueIsDestroyedWithReceptionProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		boolean CanBreak = false;
		double NBT = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		double ID = 0;
		ID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
		X = x;
		Y = y;
		Z = z;
		NBT = getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "queue");
		CanBreak = true;
		while (CanBreak) {
			CanBreak = false;
			for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
				if (IsQueueRugForRestaurantProcedure.execute(world, X + directioniterator.getStepX(), Y, Z + directioniterator.getStepZ(), ID)
						&& getBlockNBTNumber(world, BlockPos.containing(directioniterator.getStepX() + X, Y, directioniterator.getStepZ() + Z), "queue") == NBT + 1) {
					{
						BlockPos _pos = BlockPos.containing(directioniterator.getStepX() + X, Y, directioniterator.getStepZ() + Z);
						Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(directioniterator.getStepX() + X, Y, directioniterator.getStepZ() + Z), null);
						world.destroyBlock(_pos, false);
					}
					X = directioniterator.getStepX() + X;
					Z = directioniterator.getStepZ() + Z;
					NBT = NBT + 1;
					CanBreak = true;
					break;
				} else if (IsQueueRugForRestaurantProcedure.execute(world, X + directioniterator.getStepX(), Y + 1, Z + directioniterator.getStepZ(), ID)
						&& getBlockNBTNumber(world, BlockPos.containing(directioniterator.getStepX() + X, Y + 1, directioniterator.getStepZ() + Z), "queue") == NBT + 1) {
					{
						BlockPos _pos = BlockPos.containing(directioniterator.getStepX() + X, Y + 1, directioniterator.getStepZ() + Z);
						Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(directioniterator.getStepX() + X, Y + 1, directioniterator.getStepZ() + Z), null);
						world.destroyBlock(_pos, false);
					}
					X = directioniterator.getStepX() + X;
					Y = Y + 1;
					Z = directioniterator.getStepZ() + Z;
					NBT = NBT + 1;
					CanBreak = true;
					break;
				} else if (IsQueueRugForRestaurantProcedure.execute(world, X + directioniterator.getStepX(), Y - 1, Z + directioniterator.getStepZ(), ID)
						&& getBlockNBTNumber(world, BlockPos.containing(directioniterator.getStepX() + X, Y - 1, directioniterator.getStepZ() + Z), "queue") == NBT + 1) {
					{
						BlockPos _pos = BlockPos.containing(directioniterator.getStepX() + X, Y - 1, directioniterator.getStepZ() + Z);
						Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(directioniterator.getStepX() + X, Y - 1, directioniterator.getStepZ() + Z), null);
						world.destroyBlock(_pos, false);
					}
					X = directioniterator.getStepX() + X;
					Y = Y - 1;
					Z = directioniterator.getStepZ() + Z;
					NBT = NBT + 1;
					CanBreak = true;
					break;
				}
			}
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}