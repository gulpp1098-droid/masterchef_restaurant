package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class GetFoodDataFromGroupTierProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, double tier, String groupName) {
		if (groupName == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		databaseObject = ReadFoodDatabaseProcedure.execute(world);
		if (databaseObject.has(groupName)) {
			groupObject = databaseObject.get(groupName).getAsJsonObject();
			tiersObject = groupObject.get("tiers").getAsJsonObject();
			if (tiersObject.has((new java.text.DecimalFormat("0").format(tier)))) {
				tierArray = tiersObject.get((new java.text.DecimalFormat("0").format(tier))).getAsJsonArray();
			}
		}
		return tierArray;
	}
}