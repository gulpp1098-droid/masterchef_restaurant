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
		double missingFoodOrders = 0;
		double extraFoodOrders = 0;
		double wrongFoodOrders = 0;
		boolean spawned = false;
		boolean serviceNotesAvailable = false;
		serviceNotesAvailable = true;
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
				weight = memberObject.get("potential_base_exp").getAsDouble();
				if (memberObject.get("critic").getAsBoolean()) {
					weight = weight * 5;
				}
				if (spawned) {
					if (!memberObject.get("result_recorded").getAsBoolean()) {
						return result;
					}
					if (memberObject.get("order_served").getAsBoolean()) {
						if (memberObject.has("has_missing_food") && memberObject.has("has_extra_food") && memberObject.has("has_wrong_food")) {
							if (memberObject.get("has_missing_food").getAsBoolean()) {
								missingFoodOrders = missingFoodOrders + 1;
							}
							if (memberObject.get("has_extra_food").getAsBoolean()) {
								extraFoodOrders = extraFoodOrders + 1;
							}
							if (memberObject.get("has_wrong_food").getAsBoolean()) {
								wrongFoodOrders = wrongFoodOrders + 1;
							}
						} else {
							serviceNotesAvailable = false;
						}
					}
					weightedSatisfaction = weightedSatisfaction + weight * memberObject.get("final_satisfaction").getAsDouble();
				} else {
					weight = weight * 0.5;
				}
				totalBaseExp = totalBaseExp + weight;
				memberIndex = memberIndex + 1;
			}
			groupIndex = groupIndex + 1;
		}
		if (totalBaseExp > 0) {
			satisfaction = weightedSatisfaction / totalBaseExp;
			if (satisfaction < 15) {
				multiplier = -0.5;
			} else if (satisfaction < 30) {
				multiplier = -0.25;
			} else if (satisfaction < 50) {
				multiplier = 0;
			} else if (satisfaction < 75) {
				multiplier = 0.5;
			} else if (satisfaction < 95) {
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
		result.addProperty("orders_missing_food", missingFoodOrders);
		result.addProperty("orders_extra_food", extraFoodOrders);
		result.addProperty("orders_wrong_food", wrongFoodOrders);
		result.addProperty("service_notes_available", serviceNotesAvailable);
		result.addProperty("ready", true);
		return result;
	}
}