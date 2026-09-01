package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Array;

import java.io.File;

public class DebugShowAllListsProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		double Index = 0;
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		Index = 0;
		Array = GetRestaurantsListArrayProcedure.execute(world);
		for (int _i1 = 0; _i1 < (int) Array.size(); _i1++) {
			RestaurantObject = Array.get((int) Index).getAsJsonObject();
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(
						Component.literal((new java.text.DecimalFormat("###").format(RestaurantObject.get("ID").getAsDouble()) + " / " + RestaurantObject.get("name").getAsString() + " / " + RestaurantObject.get("ownerName").getAsString() + " / "
								+ new java.text.DecimalFormat("###").format(RestaurantObject.get("level").getAsDouble()) + " Lvl / " + new java.text.DecimalFormat("###").format(RestaurantObject.get("reputation").getAsDouble()) + " Rp / "
								+ RestaurantObject.get("coowners").getAsJsonArray() + " / Locations: " + RestaurantObject.get("locations").getAsJsonArray() + " / Open: " + RestaurantObject.get("open").getAsBoolean() + " / Tables: "
								+ RestaurantObject.get("tables").getAsJsonArray() + " / Reception: " + RestaurantObject.get("reception").getAsJsonArray() + " / " + RestaurantObject.get("menu").getAsJsonArray())),
						false);
			Index = Index + 1;
		}
	}
}