package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class RugQueueIsDestroyedProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPlayer());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean CanBreak = false;
		String receptionString = "";
		double NBT = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		double ID = 0;
		double receX = 0;
		double receY = 0;
		double receZ = 0;
		double newNBT = 0;
		if (MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(x, y, z))).getBlock()) {
			ID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
			if (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "queue") > 0) {
				if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID == ID) {
					X = x;
					Y = y;
					Z = z;
					NBT = getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "queue");
					newNBT = NBT - 1;
					receptionString = GetRestaurantStringParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, ID), "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
							MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
					receX = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(0, receptionString));
					receY = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(1, receptionString));
					receZ = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(2, receptionString));
					SetNumberNBTProcedure.execute(world, receX, receY, receZ, NBT - 1, "queue");
					for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
						if (MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(directioniterator.getStepX() + X, Y, directioniterator.getStepZ() + Z))).getBlock()
								&& getBlockNBTNumber(world, BlockPos.containing(directioniterator.getStepX() + X, Y, directioniterator.getStepZ() + Z), "queue") == newNBT) {
							SetStringNBTProcedure.execute(world, receX, receY, receZ, "last_rug", (directioniterator.getStepX() + X + 0.5) + ":" + Y + ":" + (directioniterator.getStepZ() + Z + 0.5));
							break;
						}
					}
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
				} else {
					if (event instanceof ICancellableEvent _cancellable) {
						_cancellable.setCanceled(true);
					}
				}
			} else {
				if (event instanceof ICancellableEvent _cancellable) {
					_cancellable.setCanceled(true);
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