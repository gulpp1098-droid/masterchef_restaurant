package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class PrepareModDataForStartupProcedure {
	public static com.google.gson.JsonObject execute(LevelAccessor world, com.google.gson.JsonObject startupData) {
		if (startupData == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject catalogObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject clientsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject contextObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject filesObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tradeBoard = new com.google.gson.JsonObject();
		com.google.gson.JsonObject member = new com.google.gson.JsonObject();
		com.google.gson.JsonObject otherRestaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject startupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject shiftResult = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonObject otherGroup = new com.google.gson.JsonObject();
		com.google.gson.JsonObject unlock = new com.google.gson.JsonObject();
		com.google.gson.JsonObject defaultStatsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject otherSchedule = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject schedule = new com.google.gson.JsonObject();
		com.google.gson.JsonObject unlocksObject = new com.google.gson.JsonObject();
		double unlockIndex = 0;
		double matchingIds = 0;
		double groupId = 0;
		double leaders = 0;
		double restaurantId = 0;
		double nextId = 0;
		double memberIndex = 0;
		double searchIndex = 0;
		double groupIndex = 0;
		double scheduleIndex = 0;
		double currentDay = 0;
		double clientsDay = 0;
		double restaurantIndex = 0;
		com.google.gson.JsonArray foods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray amounts = new com.google.gson.JsonArray();
		com.google.gson.JsonArray schedules = new com.google.gson.JsonArray();
		com.google.gson.JsonArray members = new com.google.gson.JsonArray();
		com.google.gson.JsonArray orderedUnlocks = new com.google.gson.JsonArray();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray openIds = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlocks = new com.google.gson.JsonArray();
		String phase = "";
		boolean anySpawned = false;
		startupObject = startupData;
		if (world.isClientSide()) {
			return startupObject;
		}
		defaultStatsObject = CreateDefaultRestaurantStatsProcedure.execute();
		contextObject = startupObject.get("context").getAsJsonObject();
		filesObject = startupObject.get("files").getAsJsonObject();
		catalogObject = contextObject.get("catalog").getAsJsonObject();
		currentDay = contextObject.get("day").getAsDouble();
		phase = startupObject.get("phase").getAsString();
		if ((phase).equals("prepare")) {
			if (contextObject.get("fresh").getAsBoolean()) {
				restaurantsObject.add("restaurants", restaurants);
				restaurantsObject.addProperty("last_stats_reset_day", currentDay);
				unlocksObject.add("restaurants", unlocks);
				clientsObject.add("restaurants", schedules);
				clientsObject.addProperty("database_day", currentDay);
				filesObject.add("restaurants", restaurantsObject);
				filesObject.add("unlocks", unlocksObject);
				filesObject.add("clients", clientsObject);
			}
			if (!filesObject.has("restaurants")) {
				startupObject.addProperty("error", "Missing restaurant database; cannot reconstruct existing restaurants");
				return startupObject;
			}
			restaurantsObject = filesObject.get("restaurants").getAsJsonObject();
			restaurants = restaurantsObject.get("restaurants").getAsJsonArray();
			if (!filesObject.has("unlocks")) {
				if (restaurants.size() > 0) {
					startupObject.addProperty("error", "Missing unlock database for existing restaurants");
					return startupObject;
				}
				unlocksObject.add("restaurants", unlocks);
				filesObject.add("unlocks", unlocksObject);
			}
			if (!filesObject.has("clients")) {
				if (restaurants.size() > 0) {
					startupObject.addProperty("error", "Missing client database for existing restaurants");
					return startupObject;
				}
				clientsObject.add("restaurants", schedules);
				clientsObject.addProperty("database_day", currentDay);
				filesObject.add("clients", clientsObject);
			}
			unlocksObject = filesObject.get("unlocks").getAsJsonObject();
			clientsObject = filesObject.get("clients").getAsJsonObject();
			schedules = clientsObject.get("restaurants").getAsJsonArray();
			unlocks = unlocksObject.get("restaurants").getAsJsonArray();
			if (!(restaurants.size() == unlocks.size())) {
				startupObject.addProperty("error", "Restaurant and unlock record counts differ");
				return startupObject;
			}
			restaurantIndex = 0;
			for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
				restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
				restaurantId = restaurant.get("ID").getAsDouble();
				matchingIds = 0;
				unlockIndex = 0;
				for (int _i2 = 0; _i2 < (int) unlocks.size(); _i2++) {
					unlock = unlocks.get((int) unlockIndex).getAsJsonObject();
					if (unlock.get("ID").getAsDouble() == restaurantId) {
						matchingIds = matchingIds + 1;
						orderedUnlocks.add(unlock);
					}
					unlockIndex = unlockIndex + 1;
				}
				if (!(matchingIds == 1)) {
					startupObject.addProperty("error", "Restaurant has missing or duplicate unlock record");
					return startupObject;
				}
				restaurantIndex = restaurantIndex + 1;
			}
			unlocksObject.add("restaurants", orderedUnlocks);
			if (!restaurantsObject.has("last_stats_reset_day")) {
				restaurantsObject.addProperty("last_stats_reset_day", currentDay);
			}
			if (!clientsObject.has("database_day")) {
				if (schedules.size() > 0) {
					startupObject.addProperty("error", "Missing database_day for existing client schedules");
					return startupObject;
				}
				clientsObject.addProperty("database_day", currentDay);
			}
			clientsDay = clientsObject.get("database_day").getAsDouble();
			restaurantIndex = 0;
			for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
				restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
				if (!restaurant.has("ownerName")) {
					restaurant.addProperty("ownerName", restaurant.get("owner").getAsString());
				}
				if (!restaurant.has("daily_stats")) {
					restaurant.add("daily_stats", defaultStatsObject);
				}
				if (!restaurant.has("last_day_stats")) {
					restaurant.add("last_day_stats", defaultStatsObject);
				}
				if (!restaurant.has("trade_board")) {
					tradeBoard = CreateDefaultTradeBoardDataProcedure.execute();
					restaurant.add("trade_board", tradeBoard);
				}
				tradeBoard = restaurant.get("trade_board").getAsJsonObject();
				if (tradeBoard.has("offers")) {
					offers = tradeBoard.get("offers").getAsJsonArray();
					if (offers.size() > 0 && !tradeBoard.has("day")) {
						startupObject.addProperty("error", "Trade board has offers but no saved day");
						return startupObject;
					}
				}
				if (tradeBoard.has("day")) {
					if (tradeBoard.get("day").getAsDouble() >= 0 && !tradeBoard.has("offers")) {
						startupObject.addProperty("error", "Trade board saved offers are missing");
						return startupObject;
					}
				}
				restaurantIndex = restaurantIndex + 1;
			}
			scheduleIndex = 0;
			for (int _i1 = 0; _i1 < (int) schedules.size(); _i1++) {
				schedule = schedules.get((int) scheduleIndex).getAsJsonObject();
				restaurantId = schedule.get("ID").getAsDouble();
				anySpawned = false;
				groups = schedule.get("groups").getAsJsonArray();
				groupIndex = 0;
				for (int _i2 = 0; _i2 < (int) groups.size(); _i2++) {
					group = groups.get((int) groupIndex).getAsJsonObject();
					if (group.get("spawned").getAsBoolean()) {
						anySpawned = true;
					}
					if (!group.get("spawned").getAsBoolean()) {
						members = group.get("members").getAsJsonArray();
						memberIndex = 0;
						for (int _i3 = 0; _i3 < (int) members.size(); _i3++) {
							member = members.get((int) memberIndex).getAsJsonObject();
							if (!member.has("order_served")) {
								member.addProperty("order_served", false);
							}
							if (!member.has("final_satisfaction")) {
								member.addProperty("final_satisfaction", 0);
							}
							if (!member.has("result_recorded")) {
								member.addProperty("result_recorded", false);
							}
							if (!member.has("potential_base_exp")) {
								foods = member.get("food").getAsJsonArray();
								amounts = member.get("food_amount").getAsJsonArray();
								if (!(foods.size() == amounts.size())) {
									startupObject.addProperty("error", "Food and food_amount lengths differ during migration");
									return startupObject;
								}
								member.addProperty("potential_base_exp", GetClientOrderBaseExpFromArraysProcedure.execute(amounts, foods, catalogObject));
							}
							memberIndex = memberIndex + 1;
						}
					}
					groupIndex = groupIndex + 1;
				}
				if (!schedule.has("shift_id")) {
					if (anySpawned || clientsDay < 0 || clientsDay > 375299968947) {
						startupObject.addProperty("error", "Cannot safely reconstruct client shift identity");
						return startupObject;
					}
					searchIndex = 0;
					for (int _i2 = 0; _i2 < (int) restaurants.size(); _i2++) {
						otherRestaurant = restaurants.get((int) searchIndex).getAsJsonObject();
						if (otherRestaurant.get("ID").getAsDouble() == restaurantId) {
							if (otherRestaurant.has("last_settled_shift_id") || otherRestaurant.has("last_shift_result")) {
								startupObject.addProperty("error", "Missing shift identity for a restaurant with settlement history");
								return startupObject;
							}
						}
						searchIndex = searchIndex + 1;
					}
					schedule.addProperty("shift_id", (clientsDay * 24000));
				}
				scheduleIndex = scheduleIndex + 1;
			}
		}
		if ((phase).equals("validate")) {
			restaurantsObject = filesObject.get("restaurants").getAsJsonObject();
			unlocksObject = filesObject.get("unlocks").getAsJsonObject();
			clientsObject = filesObject.get("clients").getAsJsonObject();
			restaurants = restaurantsObject.get("restaurants").getAsJsonArray();
			unlocks = unlocksObject.get("restaurants").getAsJsonArray();
			schedules = clientsObject.get("restaurants").getAsJsonArray();
			if (!(restaurants.size() == unlocks.size())) {
				startupObject.addProperty("error", "Restaurant and unlock record counts differ");
				return startupObject;
			}
			nextId = 1;
			restaurantIndex = 0;
			for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
				restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
				restaurantId = restaurant.get("ID").getAsDouble();
				searchIndex = 0;
				for (int _i2 = 0; _i2 < (int) restaurantIndex; _i2++) {
					otherRestaurant = restaurants.get((int) searchIndex).getAsJsonObject();
					if (otherRestaurant.get("ID").getAsDouble() == restaurantId) {
						startupObject.addProperty("error", "Duplicate restaurant ID");
						return startupObject;
					}
					searchIndex = searchIndex + 1;
				}
				matchingIds = 0;
				unlockIndex = 0;
				for (int _i2 = 0; _i2 < (int) unlocks.size(); _i2++) {
					unlock = unlocks.get((int) unlockIndex).getAsJsonObject();
					if (unlock.get("ID").getAsDouble() == restaurantId) {
						matchingIds = matchingIds + 1;
					}
					unlockIndex = unlockIndex + 1;
				}
				if (!(matchingIds == 1)) {
					startupObject.addProperty("error", "Restaurant has missing or duplicate unlock record");
					return startupObject;
				}
				if (restaurantId + 1 > nextId) {
					nextId = restaurantId + 1;
				}
				if (restaurant.get("open").getAsBoolean()) {
					openIds.add(restaurantId);
				}
				if (restaurant.has("last_settled_shift_id")) {
					if (!restaurant.has("last_shift_result")) {
						startupObject.addProperty("error", "Incomplete shift settlement record");
						return startupObject;
					}
				}
				if (restaurant.has("last_shift_result")) {
					if (!restaurant.has("last_settled_shift_id")) {
						startupObject.addProperty("error", "Incomplete shift settlement record");
						return startupObject;
					}
					shiftResult = restaurant.get("last_shift_result").getAsJsonObject();
					if (!shiftResult.get("ready").getAsBoolean()) {
						startupObject.addProperty("error", "Saved shift result is not ready");
						return startupObject;
					}
					if (!(shiftResult.get("shift_id").getAsDouble() == restaurant.get("last_settled_shift_id").getAsDouble())) {
						startupObject.addProperty("error", "Shift result differs from settlement marker");
						return startupObject;
					}
				}
				restaurantIndex = restaurantIndex + 1;
			}
			scheduleIndex = 0;
			for (int _i1 = 0; _i1 < (int) schedules.size(); _i1++) {
				schedule = schedules.get((int) scheduleIndex).getAsJsonObject();
				restaurantId = schedule.get("ID").getAsDouble();
				searchIndex = 0;
				for (int _i2 = 0; _i2 < (int) scheduleIndex; _i2++) {
					otherSchedule = schedules.get((int) searchIndex).getAsJsonObject();
					if (otherSchedule.get("ID").getAsDouble() == restaurantId) {
						startupObject.addProperty("error", "Duplicate client schedule ID");
						return startupObject;
					}
					searchIndex = searchIndex + 1;
				}
				matchingIds = 0;
				searchIndex = 0;
				for (int _i2 = 0; _i2 < (int) restaurants.size(); _i2++) {
					otherRestaurant = restaurants.get((int) searchIndex).getAsJsonObject();
					if (otherRestaurant.get("ID").getAsDouble() == restaurantId) {
						matchingIds = matchingIds + 1;
					}
					searchIndex = searchIndex + 1;
				}
				if (!(matchingIds == 1)) {
					startupObject.addProperty("error", "Client schedule belongs to a missing restaurant");
					return startupObject;
				}
				groups = schedule.get("groups").getAsJsonArray();
				if (schedule.get("next_spawn_group_index").getAsDouble() >= groups.size()) {
					startupObject.addProperty("error", "Next spawn group index is outside schedule");
					return startupObject;
				}
				groupIndex = 0;
				for (int _i2 = 0; _i2 < (int) groups.size(); _i2++) {
					group = groups.get((int) groupIndex).getAsJsonObject();
					groupId = group.get("group_id").getAsDouble();
					searchIndex = 0;
					for (int _i3 = 0; _i3 < (int) groupIndex; _i3++) {
						otherGroup = groups.get((int) searchIndex).getAsJsonObject();
						if (otherGroup.get("group_id").getAsDouble() == groupId) {
							startupObject.addProperty("error", "Duplicate group ID");
							return startupObject;
						}
						searchIndex = searchIndex + 1;
					}
					leaders = 0;
					members = group.get("members").getAsJsonArray();
					memberIndex = 0;
					for (int _i3 = 0; _i3 < (int) members.size(); _i3++) {
						member = members.get((int) memberIndex).getAsJsonObject();
						if (member.get("leader").getAsBoolean()) {
							leaders = leaders + 1;
						}
						foods = member.get("food").getAsJsonArray();
						amounts = member.get("food_amount").getAsJsonArray();
						if (!(foods.size() == amounts.size())) {
							startupObject.addProperty("error", "Food and food_amount lengths differ");
							return startupObject;
						}
						if (!group.get("spawned").getAsBoolean() && (member.get("order_served").getAsBoolean() || member.get("result_recorded").getAsBoolean() || !(member.get("final_satisfaction").getAsDouble() == 0))) {
							startupObject.addProperty("error", "Unspawned member has recorded visit data");
							return startupObject;
						}
						if (!member.get("result_recorded").getAsBoolean() && (member.get("order_served").getAsBoolean() || !(member.get("final_satisfaction").getAsDouble() == 0))) {
							startupObject.addProperty("error", "Unfinished result contains final visit data");
							return startupObject;
						}
						if (!member.get("order_served").getAsBoolean() && !(member.get("final_satisfaction").getAsDouble() == 0)) {
							startupObject.addProperty("error", "Unserved member has nonzero satisfaction");
							return startupObject;
						}
						memberIndex = memberIndex + 1;
					}
					if (!(leaders == 1)) {
						startupObject.addProperty("error", "Expected exactly one leader per group");
						return startupObject;
					}
					groupIndex = groupIndex + 1;
				}
				scheduleIndex = scheduleIndex + 1;
			}
			startupObject.addProperty("next_id", nextId);
			startupObject.add("open_ids", openIds);
			startupObject.addProperty("stats_day", restaurantsObject.get("last_stats_reset_day").getAsDouble());
			startupObject.addProperty("clients_day", clientsObject.get("database_day").getAsDouble());
		}
		return startupObject;
	}
}