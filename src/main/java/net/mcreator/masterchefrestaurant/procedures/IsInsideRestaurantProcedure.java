package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class IsInsideRestaurantProcedure {
	public static boolean execute(LevelAccessor world, double dependencyX, double dependencyZ, double restaurantID) {
		com.google.gson.JsonArray locationsArray = new com.google.gson.JsonArray();
		String location = "";
		double minX = 0;
		double minZ = 0;
		double maxX = 0;
		double maxZ = 0;
		double index = 0;
		double ParameterX = 0;
		double ParameterZ = 0;
		locationsArray = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "locations", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
		index = 0;
		for (int index0 = 0; index0 < (int) locationsArray.size(); index0++) {
			location = locationsArray.get((int) index).getAsString();
			ParameterX = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(0, location)) * 5;
			ParameterZ = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(1, location)) * 5;
			minX = ParameterX;
			maxX = ParameterX + 4;
			minZ = ParameterZ;
			maxZ = ParameterZ + 4;
			if (dependencyX >= minX && dependencyX <= maxX) {
				if (dependencyZ >= minZ && dependencyZ <= maxZ) {
					return true;
				}
			}
			index = index + 1;
		}
		return false;
	}
}