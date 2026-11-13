package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import java.io.File;

public class GetFoodDataFromTierProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, double tier) {
		File FoodDatabase = new File("");
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject fileObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		double foodIndex = 0;
		fileObject = ReadFoodDatabaseProcedure.execute(world);
		groupObject = fileObject.get("ingredients").getAsJsonObject();
		tiersObject = groupObject.get("tiers").getAsJsonObject();
		if (tiersObject.has(("" + (int) tier))) {
			tierArray = tiersObject.get(("" + (int) tier)).getAsJsonArray();
			for (int _i1 = 0; _i1 < (int) tierArray.size(); _i1++) {
				foodArray.add(tierArray.get((int) foodIndex).getAsJsonObject());
				foodIndex = foodIndex + 1;
			}
		}
		groupObject = fileObject.get("foods").getAsJsonObject();
		tiersObject = groupObject.get("tiers").getAsJsonObject();
		foodIndex = 0;
		if (tiersObject.has(("" + (int) tier))) {
			tierArray = tiersObject.get(("" + (int) tier)).getAsJsonArray();
			for (int _i1 = 0; _i1 < (int) tierArray.size(); _i1++) {
				foodArray.add(tierArray.get((int) foodIndex).getAsJsonObject());
				foodIndex = foodIndex + 1;
			}
		}
		return foodArray;
	}
}