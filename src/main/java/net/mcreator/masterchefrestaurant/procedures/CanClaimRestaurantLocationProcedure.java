package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class CanClaimRestaurantLocationProcedure {
	public static boolean execute(LevelAccessor world, double candidateX, double candidateZ, double restaurantID) {
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray otherLocationsArray = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double locationIndex = 0;
		double otherRestaurantID = 0;
		double baseX = 0;
		double baseZ = 0;
		double locationX = 0;
		double locationZ = 0;
		String locationString = "";
		restaurantsArray = GetRestaurantsListArrayProcedure.execute(world);
		for (int _i1 = 0; _i1 < (int) restaurantsArray.size(); _i1++) {
			otherRestaurantID = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "ID");
			if (otherRestaurantID != restaurantID) {
				otherLocationsArray = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "locations", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
				if (otherLocationsArray.size() != 0) {
					locationIndex = 0;
					for (int _i2 = 0; _i2 < (int) otherLocationsArray.size(); _i2++) {
						locationString = otherLocationsArray.get((int) locationIndex).getAsString();
						if (locationIndex == 0) {
							baseX = new Object() {
								double convert(String s) {
									try {
										return Double.parseDouble(s.trim());
									} catch (Exception e) {
									}
									return 0;
								}
							}.convert(GetPartFromStringProcedure.execute(0, locationString));
							baseZ = new Object() {
								double convert(String s) {
									try {
										return Double.parseDouble(s.trim());
									} catch (Exception e) {
									}
									return 0;
								}
							}.convert(GetPartFromStringProcedure.execute(1, locationString));
							if (Math.abs(candidateX - baseX) + Math.abs(candidateZ - baseZ) < 5) {
								return false;
							}
						} else {
							locationX = new Object() {
								double convert(String s) {
									try {
										return Double.parseDouble(s.trim());
									} catch (Exception e) {
									}
									return 0;
								}
							}.convert(GetPartFromStringProcedure.execute(0, locationString));
							locationZ = new Object() {
								double convert(String s) {
									try {
										return Double.parseDouble(s.trim());
									} catch (Exception e) {
									}
									return 0;
								}
							}.convert(GetPartFromStringProcedure.execute(1, locationString));
							if (Math.abs(candidateX - locationX) <= 1 && Math.abs(candidateZ - locationZ) <= 1) {
								return false;
							}
						}
						locationIndex = locationIndex + 1;
					}
				}
			}
			restaurantIndex = restaurantIndex + 1;
		}
		return true;
	}
}