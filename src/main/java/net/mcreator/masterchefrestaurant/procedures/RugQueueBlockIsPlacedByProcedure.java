package net.mcreator.masterchefrestaurant.procedures;

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

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class RugQueueBlockIsPlacedByProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		String ReceptionString = "";
		double X = 0;
		double Y = 0;
		double Z = 0;
		double NBTnumber = 0;
		boolean Placeable = false;
		if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
			ReceptionString = GetRestaurantStringParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
			X = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(0, ReceptionString));
			Y = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(1, ReceptionString));
			Z = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(2, ReceptionString));
			Placeable = false;
			NBTnumber = getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "queue");
			for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
				if ((world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get()
						&& getBlockNBTNumber(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()), "queue") == NBTnumber) {
					Placeable = true;
				}
			}
			if (Placeable) {
				if (RugQueueBlockLimitPlaceProcedure.execute(world, x, y, z, entity)) {
					setBlockNBTNumber(world, x, y, z, "RestaurantID", entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
					setBlockNBTNumber(world, x, y, z, "queue", (NBTnumber + 1));
					setBlockNBTNumber(world, X, Y, Z, "queue", (NBTnumber + 1));
					setBlockNBTText(world, X, Y, Z, "last_rug", ((x + 0.5) + ":" + y + ":" + (z + 0.5)));
					RugUpdateStateQueueProcedure.execute(world, x, y, z);
				} else {
					{
						BlockPos _pos = BlockPos.containing(x, y, z);
						Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
						world.destroyBlock(_pos, false);
					}
				}
			} else {
				{
					BlockPos _pos = BlockPos.containing(x, y, z);
					Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
					world.destroyBlock(_pos, false);
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("You do not have Restaurant yet!"), false);
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
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