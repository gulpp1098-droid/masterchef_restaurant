package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ClientExpPayProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
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
		client = entity;
		RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
		RestaurantLevel = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		CheckpointLevel = Math.floor(RestaurantLevel / 10) * 10;
		CheckpointReputation = GetRequiredReputationForLevelProcedure.execute(world, CheckpointLevel);
		requiredReputation = GetRequiredReputationForLevelProcedure.execute(world, RestaurantLevel + 1);
		if (client.getPersistentData().getBoolean("order_served")) {
			orderResult = GetClientOrderResultProcedure.execute(client);
			baseExp = GetClientOrderBaseExpProcedure.execute(world, client);
			satisfaction = GetClientOrderSatisfactionProcedure.execute(client);
			missingFoodTypes = orderResult.get("missing_food_types").getAsDouble();
			rewardMultiplier = (100 - missingFoodTypes * 40) / 100;
			if (rewardMultiplier > 0) {
				EXPTotal = Math.round(baseExp * (satisfaction / 100) * rewardMultiplier);
			} else {
				EXPTotal = Math.round(baseExp * ((100 - satisfaction) / 100) * rewardMultiplier);
			}
		}
		CurrentReputation = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"reputation");
		if (EXPTotal > 0) {
			if ((RestaurantLevel + 1) % 10 == 0) {
				ModifyRestaurantNumberParameterProcedure.execute(Math.min(CurrentReputation + EXPTotal, requiredReputation), RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
			} else {
				ModifyRestaurantNumberParameterProcedure.execute(CurrentReputation + EXPTotal, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
			}
		} else {
			if (!((RestaurantLevel + 1) % 10 == 0 && CurrentReputation >= requiredReputation)) {
				ModifyRestaurantNumberParameterProcedure.execute(Math.max(CurrentReputation + EXPTotal, CheckpointReputation), RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
			}
		}
		RealReputationChange = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"reputation") - CurrentReputation;
		ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndex, RealReputationChange, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"daily_stats", "reputation_change");
		RestaurantLevelUpCheckProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
	}
}