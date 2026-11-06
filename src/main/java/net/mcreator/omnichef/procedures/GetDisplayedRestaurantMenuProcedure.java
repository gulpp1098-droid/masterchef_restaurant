package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class GetDisplayedRestaurantMenuProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonArray menu = new com.google.gson.JsonArray();
		restaurant = FindRestaurantInfoByIndexViaIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
		menu = restaurant.get("menu").getAsJsonArray();
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).MenuShowNext) {
			menu = restaurant.get("next_menu").getAsJsonArray();
		} else {
			if (!restaurant.get("open").getAsBoolean() && (!restaurant.get("menu_advance_ready").getAsBoolean() || menu.size() == 0)) {
				menu = restaurant.get("next_menu").getAsJsonArray();
			}
		}
		return menu;
	}
}