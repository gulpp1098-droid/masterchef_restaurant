package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class GetDiscoveredItemsProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double index = 0;
		com.google.gson.JsonArray ingredients = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray result = new com.google.gson.JsonArray();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
		if (restaurantIndex >= 0) {
			ingredients = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked_ingredients", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			foods = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked_foods", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			for (int _i1 = 0; _i1 < (int) ingredients.size(); _i1++) {
				result.add(ingredients.get((int) index).getAsString());
				index = index + 1;
			}
			index = 0;
			for (int _i1 = 0; _i1 < (int) foods.size(); _i1++) {
				result.add(foods.get((int) index).getAsString());
				index = index + 1;
			}
		}
		return result;
	}
}