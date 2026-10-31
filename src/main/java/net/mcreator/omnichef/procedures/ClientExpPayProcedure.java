package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

public class ClientExpPayProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Entity owner = null;
		double CurrentReputation = 0;
		double EXPTotal = 0;
		double RestaurantIndex = 0;
		double RestaurantLevel = 0;
		double CheckpointReputation = 0;
		double CheckpointLevel = 0;
		double RealReputationChange = 0;
		double requiredReputation = 0;
		double baseExp = 0;
		double satisfaction = 0;
		double missingFoodTypes = 0;
		double rewardMultiplier = 0;
		com.google.gson.JsonObject orderResult = new com.google.gson.JsonObject();
		com.google.gson.JsonArray orderFoods = new com.google.gson.JsonArray();
		String ownerUUID = "";
		client = entity;
		entity.getPersistentData().putBoolean("exp_paid", true);
	}
}