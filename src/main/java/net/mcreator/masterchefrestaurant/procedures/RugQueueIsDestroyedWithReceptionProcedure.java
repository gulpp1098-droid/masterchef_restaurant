package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class RugQueueIsDestroyedWithReceptionProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean CanBreak = false;
		double NBT = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		double ID = 0;
		ID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
		if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID == ID) {
			X = x;
			Y = y;
			Z = z;
			NBT = getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "queue");
			CanBreak = true;
			while (CanBreak) {
				CanBreak = false;
				for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
					if (MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(directioniterator.getStepX() + X, Y, directioniterator.getStepZ() + Z))).getBlock()
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
					} else if (MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(directioniterator.getStepX() + X, Y + 1, directioniterator.getStepZ() + Z))).getBlock()
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
					} else if (MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(directioniterator.getStepX() + X, Y - 1, directioniterator.getStepZ() + Z))).getBlock()
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
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}