package net.mcreator.omnichef.procedures;

public class CalculateRestaurantShiftResultProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject restaurantData) {
		if (restaurantData == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject result = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject memberObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantDataObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		com.google.gson.JsonArray members = new com.google.gson.JsonArray();
		double groupIndex = 0;
		double memberIndex = 0;
		double weight = 0;
		double totalBaseExp = 0;
		double weightedSatisfaction = 0;
		double satisfaction = 0;
		double multiplier = 0;
		boolean spawned = false;
		result.addProperty("ready", false);
		restaurantDataObject = restaurantData;
		groups = restaurantDataObject.get("groups").getAsJsonArray();
		for (int _i1 = 0; _i1 < (int) groups.size(); _i1++) {
			groupObject = groups.get((int) groupIndex).getAsJsonObject();
			spawned = groupObject.get("spawned").getAsBoolean();
			members = groupObject.get("members").getAsJsonArray();
			memberIndex = 0;
			for (int _i2 = 0; _i2 < (int) members.size(); _i2++) {
				memberObject = members.get((int) memberIndex).getAsJsonObject();
				if (!memberObject.get("critic").getAsBoolean()) {
					weight = memberObject.get("potential_base_exp").getAsDouble();
					if (spawned) {
						if (!memberObject.get("result_recorded").getAsBoolean()) {
							return result;
						}
						weightedSatisfaction = weight * memberObject.get("final_satisfaction").getAsDouble();
					} else {
						weight = weight * 0.5;
					}
					totalBaseExp = totalBaseExp + weight;
				}
				memberIndex = memberIndex + 1;
			}
			groupIndex = groupIndex + 1;
		}
		if (totalBaseExp > 0) {
			satisfaction = weightedSatisfaction / totalBaseExp;
			if (totalBaseExp < 15) {
				multiplier = -0.5;
			} else if (totalBaseExp < 30) {
				multiplier = -0.25;
			} else if (totalBaseExp < 50) {
				multiplier = 0;
			} else if (totalBaseExp < 75) {
				multiplier = 0.5;
			} else if (totalBaseExp < 95) {
				multiplier = 1;
			} else {
				multiplier = 1.2;
			}
		}
		result.addProperty("shift_id", restaurantDataObject.get("shift_id").getAsDouble());
		result.addProperty("base_exp", totalBaseExp);
		result.addProperty("satisfaction", satisfaction);
		result.addProperty("multiplier", multiplier);
		result.addProperty("reputation_change", Math.round(totalBaseExp * multiplier));
		result.addProperty("ready", true);
		return result;
	}
}