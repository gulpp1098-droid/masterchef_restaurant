package net.mcreator.omnichef.procedures;

public class FinishActiveGroupDataProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject restaurantData, double shiftID, String groupKey) {
		if (restaurantData == null || groupKey == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject completedGroups = new com.google.gson.JsonObject();
		restaurant = restaurantData;
		if (restaurant.has("group_completion_shift")) {
			if (restaurant.get("group_completion_shift").getAsDouble() == shiftID) {
				completedGroups = restaurant.get("completed_groups").getAsJsonObject();
			}
		}
		if (completedGroups.has(groupKey)) {
			return restaurant;
		}
		completedGroups.addProperty(groupKey, true);
		restaurant.add("completed_groups", completedGroups);
		completedGroups.addProperty("group_completion_shift", shiftID);
		completedGroups.addProperty("active_groups", Math.max(0, restaurant.get("active_group").getAsDouble() - 1));
		return restaurant;
	}
}