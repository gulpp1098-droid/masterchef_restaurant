package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.OmnichefMod;

public class SendFoodRolesToPlayerProcedure {
	public static void execute(LevelAccessor world, Entity entity, com.google.gson.JsonObject database) {
		if (entity == null || database == null)
			return;
		com.google.gson.JsonObject lookup = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiers = new com.google.gson.JsonObject();
		com.google.gson.JsonObject food = new com.google.gson.JsonObject();
		com.google.gson.JsonObject batch = new com.google.gson.JsonObject();
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject entry = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foods = new com.google.gson.JsonArray();
		double tierIndex = 0;
		double foodIndex = 0;
		double batchCount = 0;
		if (!world.isClientSide()) {
			SendFoodRoleBatchProcedure.execute(world, entity, batch, true);
			databaseObject = database;
			lookup = BuildFoodRoleLookupProcedure.execute(database);
			groupObject = databaseObject.get("ingredients").getAsJsonObject();
			tiers = groupObject.get("tiers").getAsJsonObject();
			tierIndex = 0;
			for (int _i1 = 0; _i1 < (int) groupObject.get("tier_count").getAsDouble(); _i1++) {
				foods = tiers.get((new java.text.DecimalFormat("0").format(tierIndex))).getAsJsonArray();
				foodIndex = 0;
				for (int _i2 = 0; _i2 < (int) foods.size(); _i2++) {
					food = foods.get((int) foodIndex).getAsJsonObject();
					entry = new Object() {
						public com.google.gson.JsonObject parse(String rawJson) {
							try {
								return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
							} catch (Exception e) {
								OmnichefMod.LOGGER.error(e);
								return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
							}
						}
					}.parse("{}");
					entry.addProperty("role", food.get("role").getAsString());
					entry.addProperty("tier", food.get("tier").getAsString());
					batch.add(food.get("id").getAsString(), entry);
					batchCount = batchCount + 1;
					if (batchCount >= 100) {
						SendFoodRoleBatchProcedure.execute(world, entity, batch, false);
						batch = new Object() {
							public com.google.gson.JsonObject parse(String rawJson) {
								try {
									return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
								} catch (Exception e) {
									OmnichefMod.LOGGER.error(e);
									return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
								}
							}
						}.parse("{}");
						batchCount = 0;
					}
					foodIndex = foodIndex + 1;
				}
				tierIndex = tierIndex + 1;
			}
			groupObject = databaseObject.get("foods").getAsJsonObject();
			tiers = groupObject.get("tiers").getAsJsonObject();
			tierIndex = 0;
			for (int _i1 = 0; _i1 < (int) groupObject.get("tier_count").getAsDouble(); _i1++) {
				foods = tiers.get((new java.text.DecimalFormat("0").format(tierIndex))).getAsJsonArray();
				foodIndex = 0;
				for (int _i2 = 0; _i2 < (int) foods.size(); _i2++) {
					food = foods.get((int) foodIndex).getAsJsonObject();
					entry = new Object() {
						public com.google.gson.JsonObject parse(String rawJson) {
							try {
								return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
							} catch (Exception e) {
								OmnichefMod.LOGGER.error(e);
								return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
							}
						}
					}.parse("{}");
					entry.addProperty("role", food.get("role").getAsString());
					entry.addProperty("tier", food.get("tier").getAsString());
					batch.add(food.get("id").getAsString(), entry);
					batchCount = batchCount + 1;
					if (batchCount >= 100) {
						SendFoodRoleBatchProcedure.execute(world, entity, batch, false);
						batch = new Object() {
							public com.google.gson.JsonObject parse(String rawJson) {
								try {
									return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
								} catch (Exception e) {
									OmnichefMod.LOGGER.error(e);
									return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
								}
							}
						}.parse("{}");
						batchCount = 0;
					}
					foodIndex = foodIndex + 1;
				}
				tierIndex = tierIndex + 1;
			}
			if (batchCount > 0) {
				SendFoodRoleBatchProcedure.execute(world, entity, batch, false);
			}
		}
	}
}