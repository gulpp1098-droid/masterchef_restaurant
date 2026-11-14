package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class AddIngredientActionExpProcedure {
	public static void execute(LevelAccessor world, double amount, double restaurantID) {
		double currentDay = 0;
		double restaurantIndex = 0;
		double bonusDay = 0;
		double bonusUsed = 0;
		double grantedExp = 0;
		double pendingUnlocks = 0;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0 && amount > 0) {
			restaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, restaurantID);
			currentDay = Math.floor(world.dayTime() / 24000d);
			bonusDay = -1;
			bonusUsed = 0;
			if (restaurantObject.has("ingredient_bonus_day")) {
				bonusDay = restaurantObject.get("ingredient_bonus_day").getAsDouble();
			}
			if (restaurantObject.has("ingredient_bonus_used")) {
				bonusUsed = restaurantObject.get("ingredient_bonus_used").getAsDouble();
			}
			if (currentDay > bonusDay) {
				bonusDay = currentDay;
				bonusUsed = 0;
			}
			pendingUnlocks = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"pending_ingredient_unlocks");
			grantedExp = Math.min(amount, Math.max(0, 20 - bonusUsed));
			if (grantedExp > 0 && pendingUnlocks < 5) {
				AddIngredientExpProcedure.execute(world, amount, restaurantID);
				ModifyRestaurantNumberParameterProcedure.execute(bonusDay, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"ingredient_bonus_day");
				ModifyRestaurantNumberParameterProcedure.execute(bonusUsed + grantedExp, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "ingredient_bonus_used");
			}
		}
	}
}