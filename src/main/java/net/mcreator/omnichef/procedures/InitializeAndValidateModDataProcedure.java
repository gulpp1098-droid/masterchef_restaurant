package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class InitializeAndValidateModDataProcedure {
	public static boolean execute(LevelAccessor world) {
		String folder = "";
		boolean success = false;
		folder = GetMasterchefWorldPathProcedure.execute(world);
		class JsonStructureValidator {
			void check(com.google.gson.JsonElement actual, com.google.gson.JsonElement pattern, String path) {
				if (actual == null || actual.isJsonNull()) {
					throw new IllegalStateException("Missing data: " + path);
				}
				if (pattern.isJsonObject()) {
					if (!actual.isJsonObject()) {
						throw new IllegalStateException("Expected object: " + path);
					}
					for (var entry : pattern.getAsJsonObject().entrySet()) {
						check(actual.getAsJsonObject().get(entry.getKey()), entry.getValue(), path + "." + entry.getKey());
					}
				} else if (pattern.isJsonArray()) {
					if (!actual.isJsonArray()) {
						throw new IllegalStateException("Expected array: " + path);
					}
					var arrayPattern = pattern.getAsJsonArray();
					if (!arrayPattern.isEmpty()) {
						int index = 0;
						for (var element : actual.getAsJsonArray()) {
							check(element, arrayPattern.get(0), path + "[" + index + "]");
							index++;
						}
					}
				} else {
					if (!actual.isJsonPrimitive()) {
						throw new IllegalStateException("Expected value: " + path);
					}
					var value = actual.getAsJsonPrimitive();
					var expected = pattern.getAsJsonPrimitive();
					boolean valid = (expected.isString() && value.isString()) || (expected.isBoolean() && value.isBoolean()) || (expected.isNumber() && value.isNumber() && Double.isFinite(value.getAsDouble()));
					if (!valid) {
						throw new IllegalStateException("Invalid value type: " + path);
					}
				}
			}

			void checkFile(java.nio.file.Path file, String pattern) throws java.io.IOException {
				try (var reader = java.nio.file.Files.newBufferedReader(file, java.nio.charset.StandardCharsets.UTF_8)) {
					check(com.google.gson.JsonParser.parseReader(reader), com.google.gson.JsonParser.parseString(pattern), file.getFileName().toString());
				}
			}
		}
		try {
			CreatingFoodListJsonProcedure.execute(world);
			CreatingClientsNameDatabaseJsonFileProcedure.execute(world);
			CreatingClientsDatabaseJsonFileProcedure.execute(world);
			CreatingRestaurantJsonFileProcedure.execute(world);
			CreatingRestaurantFoodJsonFileProcedure.execute(world);
			var directory = java.nio.file.Path.of(folder);
			var variables = net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world);
			var validator = new JsonStructureValidator();
			validator.checkFile(directory.resolve(variables.Restaurant_File_Name), """
					{
					  "last_stats_reset_day": 0,
					  "restaurants": [{
					    "ID": 0,
					    "name": "",
					    "owner": "",
					    "ownerName": "",
					    "level": 0,
					    "reputation": 0,
					    "coowners": [""],
					    "locations": [""],
					    "open": false,
					    "active_groups": 0,
					    "close_time": 0,
					    "tables": [""],
					    "reception": "",
					    "last_day_open": 0,
					    "menu": [""],
					    "next_menu": [""],
					    "menu_advance_ready": false,
					    "daily_stats": {
					      "customers_served_fully": 0,
					      "customers_served": 0,
					      "customers_lost": 0,
					      "coins_earned": 0,
					      "reputation_change": 0
					    },
					    "last_day_stats": {
					      "customers_served_fully": 0,
					      "customers_served": 0,
					      "customers_lost": 0,
					      "coins_earned": 0,
					      "reputation_change": 0
					    }
					  }]
					}
					""");
			validator.checkFile(directory.resolve(variables.RestaurantFood_File_Name), """
					{
					  "restaurants": [{
					    "ID": 0,
					    "discovery_exp": 0,
					    "stage_tier": 0,
					    "starter_unlocks_remaining": 0,
					    "pending_unlocks": 0,
					    "unlocked": [""],
					    "unlock_options": [""],
					    "pending_cards": [""]
					  }]
					}
					""");
			validator.checkFile(directory.resolve(variables.ClientsDatabase_File_Name), """
					{
					  "database_day": 0,
					  "restaurants": [{
					    "ID": 0,
					    "next_spawn_group_time": 0,
					    "next_spawn_group_index": 0,
					    "queue_start": "",
					    "groups": [{
					      "group_id": 0,
					      "spawn_time": 0,
					      "spawned": false,
					      "state": "",
					      "members": [{
					        "leader": false,
					        "critic": false,
					        "food": [""],
					        "food_amount": [0],
					        "patience": 0
					      }]
					    }]
					  }]
					}
					""");
			var restaurantIds = new java.util.ArrayList<Integer>();
			var unlockIds = new java.util.ArrayList<Integer>();
			String[] relatedFiles = {variables.Restaurant_File_Name, variables.RestaurantFood_File_Name};
			for (int fileIndex = 0; fileIndex < relatedFiles.length; fileIndex++) {
				String fileName = relatedFiles[fileIndex];
				var ids = fileIndex == 0 ? restaurantIds : unlockIds;
				var uniqueIds = new java.util.HashSet<Integer>();
				try (var reader = java.nio.file.Files.newBufferedReader(directory.resolve(fileName), java.nio.charset.StandardCharsets.UTF_8)) {
					var restaurants = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("restaurants");
					for (int index = 0; index < restaurants.size(); index++) {
						int id = restaurants.get(index).getAsJsonObject().get("ID").getAsBigDecimal().intValueExact();
						if (id <= 0) {
							throw new IllegalStateException(fileName + ": invalid restaurant ID at index " + index);
						}
						if (!uniqueIds.add(id)) {
							throw new IllegalStateException(fileName + ": duplicate restaurant ID " + id);
						}
						ids.add(id);
					}
				}
			}
			if (!restaurantIds.equals(unlockIds)) {
				throw new IllegalStateException("Restaurant records differ between " + variables.Restaurant_File_Name + " and " + variables.RestaurantFood_File_Name + "; IDs: " + restaurantIds + " / " + unlockIds);
			}
			success = true;
		} catch (Exception exception) {
			net.mcreator.omnichef.OmnichefMod.LOGGER.error("Omnichef: data initialization or validation failed.", exception);
		}
		return success;
	}
}