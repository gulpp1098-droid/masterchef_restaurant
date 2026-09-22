package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class CanDeleteRestaurantProcedure {
	public static boolean execute(LevelAccessor world, Entity entity, double RestaurantID) {
		if (entity == null)
			return false;
		double restaurantID = 0;
		double restaurantIndex = 0;
		double tableIndex = 0;
		Entity owner = null;
		com.google.gson.JsonArray tableList = new com.google.gson.JsonArray();
		String tableX = "";
		String tableY = "";
		String tableZ = "";
		String tableString = "";
		boolean tableReady = false;
		restaurantID = RestaurantID;
		owner = entity;
		if (restaurantID <= 0) {
			return false;
		}
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex < 0) {
			return false;
		}
		if ((GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"owner")).equals(owner.getStringUUID())) {
			if (!GetRestaurantLogicParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")
					&& GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
							MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups") == 0) {
				tableList = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "tables", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
				tableIndex = 0;
				for (int _i1 = 0; _i1 < (int) tableList.size(); _i1++) {
					tableString = tableList.get((int) tableIndex).getAsString();
					tableX = GetPartFromStringProcedure.execute(0, tableString);
					tableY = GetPartFromStringProcedure.execute(1, tableString);
					tableZ = GetPartFromStringProcedure.execute(2, tableString);
					if (getBlockNBTNumber(world, BlockPos.containing(new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(tableX), new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(tableY), new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(tableZ)), "coins") > 0) {
						return false;
					}
					tableIndex = tableIndex + 1;
				}
				return true;
			}
		}
		return false;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}