package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class RugQueueBlockLimitPlaceProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		String ReceptionString = "";
		double X = 0;
		double Y = 0;
		double Z = 0;
		double NBTnumber = 0;
		double RestaurantLevel = 0;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get()) {
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
			NBTnumber = getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "queue");
			RestaurantLevel = GetRestaurantNumberParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
			if (!(NBTnumber <= Math.floor(RestaurantLevel / 10))) {
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