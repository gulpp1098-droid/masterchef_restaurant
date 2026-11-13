package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class InitializeAndValidateModDataProcedure {
	public static boolean execute(LevelAccessor world) {
		String folder = "";
		boolean success = false;
		folder = GetMasterchefWorldPathProcedure.execute(world);
		class StartupDataEngine {
			final java.nio.file.Path directory;
			final String[] names;
			final long day;
			final com.google.gson.JsonObject catalog;
			final com.google.gson.JsonObject schemas;
			java.util.function.BiFunction<com.google.gson.JsonArray, com.google.gson.JsonArray, Double> baseExp;
			final java.util.function.Consumer<String> log;
			int maximumId;
			boolean allowFreshDataset = true;
			final java.util.List<Double> openRestaurantIds = new java.util.ArrayList<>();
			long statisticsDay;
			long clientsDay;

			public StartupDataEngine(java.nio.file.Path directory, String[] names, long day, com.google.gson.JsonObject catalog, com.google.gson.JsonObject schemas, java.util.function.Consumer<String> log) throws java.io.IOException {
				this.directory = directory.toAbsolutePath().normalize();
				this.names = names.clone();
				this.day = day;
				this.catalog = catalog;
				this.schemas = schemas.deepCopy();
				this.log = log;
				java.util.Set<String> unique = new java.util.HashSet<>();
				for (String name : names) {
					if (name.isBlank() || !java.nio.file.Path.of(name).getFileName().toString().equals(name) || name.contains("\\") || name.equals(".") || name.equals("..") || !unique.add(name)) {
						throw new java.io.IOException("Invalid or duplicate data filename: " + name);
					}
				}
				if (names.length != 3) {
					throw new java.io.IOException("Expected three data filenames");
				}
				checkSchemas();
			}

			IllegalStateException bad(String path, String reason) {
				return new IllegalStateException(path + ": " + reason);
			}

			com.google.gson.JsonElement readValue(com.google.gson.stream.JsonReader reader, String path, int depth) throws java.io.IOException {
				if (depth > 128)
					throw bad(path, "JSON nesting exceeds 128");
				switch (reader.peek()) {
					case BEGIN_OBJECT :
						com.google.gson.JsonObject object = new com.google.gson.JsonObject();
						reader.beginObject();
						while (reader.hasNext()) {
							String key = reader.nextName();
							if (object.has(key)) {
								throw bad(path + "." + key, "duplicate JSON key");
							}
							object.add(key, readValue(reader, path + "." + key, depth + 1));
						}
						reader.endObject();
						return object;
					case BEGIN_ARRAY :
						com.google.gson.JsonArray array = new com.google.gson.JsonArray();
						reader.beginArray();
						while (reader.hasNext()) {
							array.add(readValue(reader, path + "[" + array.size() + "]", depth + 1));
						}
						reader.endArray();
						return array;
					case STRING :
						return new com.google.gson.JsonPrimitive(reader.nextString());
					case NUMBER :
						return new com.google.gson.JsonPrimitive(new java.math.BigDecimal(reader.nextString()));
					case BOOLEAN :
						return new com.google.gson.JsonPrimitive(reader.nextBoolean());
					case NULL :
						reader.nextNull();
						return com.google.gson.JsonNull.INSTANCE;
					default :
						throw bad(path, "expected JSON value");
				}
			}

			com.google.gson.JsonObject read(java.nio.file.Path file) throws java.io.IOException {
				try (com.google.gson.stream.JsonReader reader = new com.google.gson.stream.JsonReader(java.nio.file.Files.newBufferedReader(file, java.nio.charset.StandardCharsets.UTF_8))) {
					reader.setLenient(false);
					com.google.gson.JsonElement value = readValue(reader, file.getFileName().toString(), 0);
					if (reader.peek() != com.google.gson.stream.JsonToken.END_DOCUMENT) {
						throw bad(file.toString(), "trailing JSON data");
					}
					return object(value, file.getFileName().toString());
				}
			}

			com.google.gson.JsonObject object(com.google.gson.JsonElement value, String path) {
				if (value == null || !value.isJsonObject()) {
					throw bad(path, "required object missing or wrong type");
				}
				return value.getAsJsonObject();
			}

			com.google.gson.JsonArray array(com.google.gson.JsonObject object, String key, String path) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonArray()) {
					throw bad(path + "." + key, "required array missing or wrong type");
				}
				return value.getAsJsonArray();
			}

			String text(com.google.gson.JsonObject object, String key, String path, boolean emptyAllowed) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
					throw bad(path + "." + key, "required string missing or wrong type");
				}
				String result = value.getAsString();
				if (!emptyAllowed && result.isBlank()) {
					throw bad(path + "." + key, "empty string");
				}
				return result;
			}

			double number(com.google.gson.JsonObject object, String key, String path) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber() || !Double.isFinite(value.getAsDouble())) {
					throw bad(path + "." + key, "required finite number missing or wrong type");
				}
				return value.getAsDouble();
			}

			long integer(com.google.gson.JsonObject object, String key, String path, long minimum, long maximum) {
				number(object, key, path);
				long value;
				try {
					value = object.get(key).getAsBigDecimal().longValueExact();
				} catch (ArithmeticException exception) {
					throw bad(path + "." + key, "expected an integer");
				}
				if (value < minimum || value > maximum) {
					throw bad(path + "." + key, "integer out of range");
				}
				return value;
			}

			boolean bool(com.google.gson.JsonObject object, String key, String path) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
					throw bad(path + "." + key, "required boolean missing or wrong type");
				}
				return value.getAsBoolean();
			}

			void supply(com.google.gson.JsonObject object, String key, com.google.gson.JsonElement value, String path) {
				if (!object.has(key)) {
					object.add(key, value.deepCopy());
					log.accept("Filled missing field " + path + "." + key);
				}
			}

			void strings(com.google.gson.JsonObject object, String key, String path, int coordinateParts) {
				com.google.gson.JsonArray values = array(object, key, path);
				for (int index = 0; index < values.size(); index++) {
					com.google.gson.JsonElement value = values.get(index);
					String location = path + "." + key + "[" + index + "]";
					if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || value.getAsString().isBlank()) {
						throw bad(location, "expected nonempty string");
					}
					if (coordinateParts > 0) {
						coordinates(value.getAsString(), coordinateParts, location);
					}
				}
			}

			void coordinates(String value, int parts, String path) {
				String[] coordinates = value.split(":", -1);
				if (coordinates.length != parts) {
					throw bad(path, "invalid coordinate count");
				}
				for (String coordinate : coordinates) {
					try {
						java.math.BigDecimal numeric = new java.math.BigDecimal(coordinate.trim());
						if (numeric.compareTo(java.math.BigDecimal.valueOf(Integer.MIN_VALUE)) < 0 || numeric.compareTo(java.math.BigDecimal.valueOf(Integer.MAX_VALUE)) > 0) {
							throw bad(path, "coordinate out of range");
						}
						if (parts == 2)
							numeric.intValueExact();
					} catch (NumberFormatException | ArithmeticException exception) {
						throw bad(path, "invalid coordinate: " + coordinate);
					}
				}
			}

			com.google.gson.JsonObject fields(com.google.gson.JsonObject data, String key) {
				if (!data.has(key))
					return new com.google.gson.JsonObject();
				return object(data.get(key), "schema." + key);
			}

			void checkSchemas() {
				java.util.Set<String> rules = java.util.Set.of("string", "nonempty_string", "uuid", "boolean", "number", "integer", "positive_integer", "nonnegative_integer", "object", "array", "nonempty_string_array", "positive_integer_array",
						"coordinates_2_array", "coordinates_3_array", "coordinates_3_or_empty");
				for (var entry : schemas.entrySet()) {
					String name = entry.getKey();
					var schema = object(entry.getValue(), "schema." + name);
					var defaults = fields(schema, "defaults");
					var required = fields(schema, "required");
					var minimums = fields(schema, "minimums");
					if (schema.has("negative_allowed")) {
						java.util.Set<String> negative = new java.util.HashSet<>();
						for (var value : array(schema, "negative_allowed", "schema." + name)) {
							if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || !defaults.has(value.getAsString())) {
								throw bad("schema." + name, "invalid negative_allowed entry");
							}
							negative.add(value.getAsString());
						}
						for (var field : defaults.entrySet()) {
							number(defaults, field.getKey(), "schema." + name + ".defaults");
							required.addProperty(field.getKey(), "number");
							if (!negative.contains(field.getKey()) && !minimums.has(field.getKey())) {
								minimums.addProperty(field.getKey(), 0);
							}
						}
					}
					schema.add("defaults", defaults);
					schema.add("required", required);
					schema.add("minimums", minimums);
					for (String key : new String[]{"optional", "maximums", "max_lengths", "array_items", "object_schemas"}) {
						schema.add(key, fields(schema, key));
					}
					for (String key : new String[]{"required", "optional"}) {
						for (var field : fields(schema, key).entrySet()) {
							if (!field.getValue().isJsonPrimitive() || !field.getValue().getAsJsonPrimitive().isString() || !rules.contains(field.getValue().getAsString())) {
								throw bad("schema." + name + "." + key + "." + field.getKey(), "unknown field rule");
							}
							if (key.equals("optional") && required.has(field.getKey())) {
								throw bad("schema." + name, "field is both required and optional: " + field.getKey());
							}
						}
					}
					for (String key : new String[]{"minimums", "maximums", "max_lengths"}) {
						for (var field : fields(schema, key).entrySet()) {
							String fieldName = field.getKey();
							number(fields(schema, key), fieldName, "schema." + name + "." + key);
							if (!required.has(fieldName) && !fields(schema, "optional").has(fieldName) && !defaults.has(fieldName)) {
								throw bad("schema." + name, "constraint for undeclared field: " + fieldName);
							}
							if (key.equals("max_lengths")) {
								integer(fields(schema, key), fieldName, "schema." + name, 0, Long.MAX_VALUE);
							}
						}
					}
					for (var field : minimums.entrySet()) {
						var maximums = fields(schema, "maximums");
						if (maximums.has(field.getKey()) && field.getValue().getAsBigDecimal().compareTo(maximums.get(field.getKey()).getAsBigDecimal()) > 0) {
							throw bad("schema." + name, "minimum exceeds maximum: " + field.getKey());
						}
					}
					for (String key : new String[]{"array_items", "object_schemas"}) {
						for (var field : fields(schema, key).entrySet()) {
							String target = text(fields(schema, key), field.getKey(), "schema." + name, false);
							if (!schemas.has(target)) {
								throw bad("schema." + name, "unknown schema reference: " + target);
							}
							if (!required.has(field.getKey()) && !fields(schema, "optional").has(field.getKey()) && !defaults.has(field.getKey())) {
								throw bad("schema." + name, "reference for undeclared field: " + field.getKey());
							}
						}
					}
				}
				for (String name : new String[]{"restaurants_file", "unlocks_file", "clients_file", "restaurant", "restaurant_stats", "trade_board", "trade_offer", "restaurant_unlocks", "client_schedule", "client_group", "client_member",
						"shift_result"}) {
					if (!schemas.has(name)) {
						throw bad("schemas", "missing schema: " + name);
					}
				}
			}

			void fillDefaults(com.google.gson.JsonObject data, com.google.gson.JsonObject defaults, String path, int depth) {
				if (depth > 128) {
					throw bad(path, "default nesting exceeds 128");
				}
				for (var entry : defaults.entrySet()) {
					String key = entry.getKey();
					var expected = entry.getValue();
					if (expected.isJsonNull()) {
						throw bad(path + "." + key, "null default is unsupported");
					}
					supply(data, key, expected, path);
					if (expected.isJsonObject()) {
						fillDefaults(object(data.get(key), path + "." + key), expected.getAsJsonObject(), path + "." + key, depth + 1);
					} else if (expected.isJsonArray()) {
						array(data, key, path);
					} else if (expected.getAsJsonPrimitive().isBoolean()) {
						bool(data, key, path);
					} else if (expected.getAsJsonPrimitive().isNumber()) {
						number(data, key, path);
					} else {
						text(data, key, path, true);
					}
				}
			}

			void validateType(com.google.gson.JsonObject data, String key, String rule, String path) {
				switch (rule) {
					case "string" :
						text(data, key, path, true);
						break;
					case "nonempty_string" :
						text(data, key, path, false);
						break;
					case "boolean" :
						bool(data, key, path);
						break;
					case "number" :
						number(data, key, path);
						break;
					case "integer" :
						integer(data, key, path, Long.MIN_VALUE, Long.MAX_VALUE);
						break;
					case "positive_integer" :
						integer(data, key, path, 1, Integer.MAX_VALUE);
						break;
					case "nonnegative_integer" :
						integer(data, key, path, 0, Integer.MAX_VALUE);
						break;
					case "object" :
						object(data.get(key), path + "." + key);
						break;
					case "array" :
						array(data, key, path);
						break;
					case "nonempty_string_array" :
						strings(data, key, path, 0);
						break;
					case "coordinates_2_array" :
						strings(data, key, path, 2);
						break;
					case "coordinates_3_array" :
						strings(data, key, path, 3);
						break;
					case "coordinates_3_or_empty" : {
						String value = text(data, key, path, true);
						if (!value.isEmpty()) {
							coordinates(value, 3, path + "." + key);
						}
						break;
					}
					case "uuid" : {
						String value = text(data, key, path, false);
						try {
							if (!java.util.UUID.fromString(value).toString().equalsIgnoreCase(value)) {
								throw bad(path + "." + key, "invalid UUID");
							}
						} catch (IllegalArgumentException exception) {
							throw bad(path + "." + key, "invalid UUID");
						}
						break;
					}
					case "positive_integer_array" : {
						var values = array(data, key, path);
						for (int i = 0; i < values.size(); i++) {
							var box = new com.google.gson.JsonObject();
							box.add("value", values.get(i));
							integer(box, "value", path + "." + key + "[" + i + "]", 1, Integer.MAX_VALUE);
						}
						break;
					}
					default :
						throw bad(path + "." + key, "unknown rule: " + rule);
				}
			}

			void validate(String name, com.google.gson.JsonObject data, String path, int depth) {
				if (depth > 128) {
					throw bad(path, "schema nesting exceeds 128");
				}
				var schema = object(schemas.get(name), "schema." + name);
				fillDefaults(data, fields(schema, "defaults"), path, depth);
				for (var field : fields(schema, "required").entrySet()) {
					validateType(data, field.getKey(), field.getValue().getAsString(), path);
				}
				for (var field : fields(schema, "optional").entrySet()) {
					if (data.has(field.getKey())) {
						validateType(data, field.getKey(), field.getValue().getAsString(), path);
					}
				}
				for (String kind : new String[]{"minimums", "maximums"}) {
					for (var bound : fields(schema, kind).entrySet()) {
						String key = bound.getKey();
						if (!data.has(key))
							continue;
						number(data, key, path);
						int comparison = data.get(key).getAsBigDecimal().compareTo(bound.getValue().getAsBigDecimal());
						if (kind.equals("minimums") ? comparison < 0 : comparison > 0) {
							throw bad(path + "." + key, "value outside " + kind);
						}
					}
				}
				for (var bound : fields(schema, "max_lengths").entrySet()) {
					String key = bound.getKey();
					if (!data.has(key))
						continue;
					long length = array(data, key, path).size();
					if (java.math.BigDecimal.valueOf(length).compareTo(bound.getValue().getAsBigDecimal()) > 0) {
						throw bad(path + "." + key, "array too long");
					}
				}
				for (var field : fields(schema, "object_schemas").entrySet()) {
					String key = field.getKey();
					if (data.has(key)) {
						validate(field.getValue().getAsString(), object(data.get(key), path + "." + key), path + "." + key, depth + 1);
					}
				}
				for (var field : fields(schema, "array_items").entrySet()) {
					String key = field.getKey();
					if (!data.has(key))
						continue;
					var values = array(data, key, path);
					for (int i = 0; i < values.size(); i++) {
						String itemPath = path + "." + key + "[" + i + "]";
						validate(field.getValue().getAsString(), object(values.get(i), itemPath), itemPath, depth + 1);
					}
				}
			}

			com.google.gson.JsonObject defaultsFor(String name) {
				return fields(object(schemas.get(name), "schema." + name), "defaults");
			}

			java.util.Map<Integer, com.google.gson.JsonObject> index(com.google.gson.JsonObject root, String file) {
				java.util.Map<Integer, com.google.gson.JsonObject> result = new java.util.LinkedHashMap<>();
				com.google.gson.JsonArray records = array(root, "restaurants", file);
				for (int index = 0; index < records.size(); index++) {
					String path = file + ".restaurants[" + index + "]";
					com.google.gson.JsonObject record = object(records.get(index), path);
					int id = (int) integer(record, "ID", path, 1, Integer.MAX_VALUE - 1);
					if (result.put(id, record) != null) {
						throw bad(path + ".ID", "duplicate restaurant ID " + id);
					}
				}
				return result;
			}

			com.google.gson.JsonObject emptyRoot(String dayField) {
				com.google.gson.JsonObject root = new com.google.gson.JsonObject();
				root.add("restaurants", new com.google.gson.JsonArray());
				if (dayField != null) {
					root.addProperty(dayField, day);
				}
				return root;
			}

			com.google.gson.JsonObject[] load(java.nio.file.Path source) throws java.io.IOException {
				boolean anyExists = false;
				for (String name : names) {
					anyExists |= java.nio.file.Files.exists(source.resolve(name));
				}
				if (!anyExists) {
					if (source.equals(directory) && (!allowFreshDataset || java.nio.file.Files.isDirectory(source.resolve("backups")))) {
						throw bad("dataset", "all files missing from an existing world");
					}
					return new com.google.gson.JsonObject[]{emptyRoot("last_stats_reset_day"), emptyRoot(null), emptyRoot("database_day")};
				}
				com.google.gson.JsonObject[] roots = new com.google.gson.JsonObject[3];
				for (int index = 0; index < 3; index++) {
					java.nio.file.Path file = source.resolve(names[index]);
					if (java.nio.file.Files.isRegularFile(file)) {
						roots[index] = read(file);
					}
				}
				if (roots[0] == null) {
					throw bad(names[0], "file missing from an existing dataset");
				}
				if (roots[1] == null && array(roots[0], "restaurants", names[0]).isEmpty()) {
					roots[1] = emptyRoot(null);
				}
				if (roots[2] == null && array(roots[0], "restaurants", names[0]).isEmpty()) {
					roots[2] = emptyRoot("database_day");
				}
				for (int index = 0; index < 3; index++) {
					if (roots[index] == null) {
						throw bad(names[index], "file missing from an existing dataset");
					}
				}
				return roots;
			}

			void migrateUnlocks(com.google.gson.JsonObject unlock, String path) {
				int stage = (int) integer(unlock, "stage_tier", path, 0, Integer.MAX_VALUE);
				long starter = integer(unlock, "starter_unlocks_remaining", path, 0, 3);
				strings(unlock, "unlocked", path, 0);
				strings(unlock, "pending_cards", path, 0);
				var cards = array(unlock, "pending_cards", path);
				supply(unlock, "pending_unlocks", new com.google.gson.JsonPrimitive(starter <= 0 && !cards.isEmpty() ? 1 : 0), path);
				if (!unlock.has("unlock_options")) {
					var options = new com.google.gson.JsonArray();
					java.util.Set<String> unlocked = new java.util.HashSet<>();
					for (var value : array(unlock, "unlocked", path)) {
						unlocked.add(value.getAsString());
					}
					long count = integer(catalog, "tier_count", "FoodDatabase", 1, Integer.MAX_VALUE);
					if (stage >= count) {
						throw bad(path + ".stage_tier", "cannot reconstruct options: stage exceeds food tiers");
					}
					java.util.Set<String> seen = new java.util.HashSet<>();
					for (String groupKey : java.util.List.of("ingredients", "foods")) {
						String groupPath = "FoodDatabase." + groupKey;
						var group = object(catalog.get(groupKey), groupPath);
						long groupCount = integer(group, "tier_count", groupPath, 1, Integer.MAX_VALUE);
						var tiers = object(group.get("tiers"), groupPath + ".tiers");
						for (int tier = 0; tier <= stage && tier < groupCount; tier++) {
							for (var value : array(tiers, Integer.toString(tier), groupPath + ".tiers")) {
								String food = text(object(value, groupPath + ".food"), "id", groupPath + ".food", false);
								if (!unlocked.contains(food) && seen.add(food)) {
									options.add(food);
								}
							}
						}
					}
					unlock.add("unlock_options", options);
					log.accept("Reconstructed " + path + ".unlock_options");
				}
			}

			void migrateRestaurant(com.google.gson.JsonObject restaurant, String path) {
				fillDefaults(restaurant, defaultsFor("restaurant"), path, 0);
				String owner = text(restaurant, "owner", path, false);
				supply(restaurant, "ownerName", new com.google.gson.JsonPrimitive(owner), path);
				supply(restaurant, "daily_stats", new com.google.gson.JsonObject(), path);
				supply(restaurant, "last_day_stats", new com.google.gson.JsonObject(), path);
				supply(restaurant, "trade_board", defaultsFor("trade_board"), path);
				var tradeBoard = object(restaurant.get("trade_board"), path + ".trade_board");
				if (!tradeBoard.has("day") && tradeBoard.has("offers") && !array(tradeBoard, "offers", path + ".trade_board").isEmpty()) {
					throw bad(path + ".trade_board", "offers exist but their day is missing");
				}
				if (!tradeBoard.has("offers") && tradeBoard.has("day") && number(tradeBoard, "day", path + ".trade_board") >= 0) {
					throw bad(path + ".trade_board", "saved offers are missing");
				}
				fillDefaults(tradeBoard, defaultsFor("trade_board"), path + ".trade_board", 0);
			}

			void migrateClients(com.google.gson.JsonObject root, java.util.Map<Integer, com.google.gson.JsonObject> restaurants) {
				var schedules = array(root, "restaurants", names[2]);
				if (schedules.isEmpty()) {
					supply(root, "database_day", new com.google.gson.JsonPrimitive(day), names[2]);
				}
				clientsDay = integer(root, "database_day", names[2], -1, Long.MAX_VALUE);
				for (var entry : index(root, names[2]).entrySet()) {
					var schedule = entry.getValue();
					String path = names[2] + ".restaurant(ID=" + entry.getKey() + ")";
					var groups = array(schedule, "groups", path);
					boolean anySpawned = false;
					for (int i = 0; i < groups.size(); i++) {
						String groupPath = path + ".groups[" + i + "]";
						var group = object(groups.get(i), groupPath);
						boolean spawned = bool(group, "spawned", groupPath);
						anySpawned |= spawned;
						var members = array(group, "members", groupPath);
						for (int j = 0; j < members.size(); j++) {
							String memberPath = groupPath + ".members[" + j + "]";
							var member = object(members.get(j), memberPath);
							if (!spawned) {
								supply(member, "order_served", new com.google.gson.JsonPrimitive(false), memberPath);
								supply(member, "final_satisfaction", new com.google.gson.JsonPrimitive(0), memberPath);
								supply(member, "result_recorded", new com.google.gson.JsonPrimitive(false), memberPath);
								if (!member.has("potential_base_exp")) {
									validateType(member, "food", "nonempty_string_array", memberPath);
									validateType(member, "food_amount", "positive_integer_array", memberPath);
									var foods = array(member, "food", memberPath);
									var amounts = array(member, "food_amount", memberPath);
									if (foods.size() != amounts.size()) {
										throw bad(memberPath, "food and food_amount lengths differ");
									}
									if (baseExp == null) {
										throw bad(memberPath, "base EXP calculator is unavailable");
									}
									double value = baseExp.apply(amounts, foods);
									if (!Double.isFinite(value) || value < 0) {
										throw bad(memberPath, "cannot reconstruct potential base EXP");
									}
									supply(member, "potential_base_exp", new com.google.gson.JsonPrimitive(value), memberPath);
								}
							}
						}
					}
					if (!schedule.has("shift_id")) {
						var restaurant = restaurants.get(entry.getKey());
						if (anySpawned || clientsDay < 0 || clientsDay > Long.MAX_VALUE / 24000L || (restaurant != null && (restaurant.has("last_settled_shift_id") || restaurant.has("last_shift_result")))) {
							throw bad(path + ".shift_id", "cannot safely reconstruct an existing shift identity");
						}
						supply(schedule, "shift_id", new com.google.gson.JsonPrimitive(clientsDay * 24000L), path);
					}
				}
			}

			void checkClients(com.google.gson.JsonObject root) {
				for (var entry : index(root, names[2]).entrySet()) {
					var schedule = entry.getValue();
					String path = names[2] + ".restaurant(ID=" + entry.getKey() + ")";
					var groups = array(schedule, "groups", path);
					long nextIndex = integer(schedule, "next_spawn_group_index", path, -1, Integer.MAX_VALUE);
					if (nextIndex >= groups.size()) {
						throw bad(path + ".next_spawn_group_index", "group index outside schedule");
					}
					java.util.Set<Long> ids = new java.util.HashSet<>();
					for (int i = 0; i < groups.size(); i++) {
						String groupPath = path + ".groups[" + i + "]";
						var group = object(groups.get(i), groupPath);
						long groupId = integer(group, "group_id", groupPath, 0, Integer.MAX_VALUE);
						if (!ids.add(groupId)) {
							throw bad(groupPath, "duplicate group ID");
						}
						boolean spawned = bool(group, "spawned", groupPath);
						var members = array(group, "members", groupPath);
						int leaders = 0;
						for (int j = 0; j < members.size(); j++) {
							String memberPath = groupPath + ".members[" + j + "]";
							var member = object(members.get(j), memberPath);
							if (bool(member, "leader", memberPath)) {
								leaders++;
							}
							if (array(member, "food", memberPath).size() != array(member, "food_amount", memberPath).size()) {
								throw bad(memberPath, "food and food_amount lengths differ");
							}
							boolean served = bool(member, "order_served", memberPath);
							boolean recorded = bool(member, "result_recorded", memberPath);
							double satisfaction = number(member, "final_satisfaction", memberPath);
							if (!spawned && (served || recorded || satisfaction != 0)) {
								throw bad(memberPath, "unspawned member has a recorded visit");
							}
							if (!recorded && (served || satisfaction != 0)) {
								throw bad(memberPath, "unfinished result contains final visit data");
							}
							if (!served && satisfaction != 0) {
								throw bad(memberPath, "unserved member has nonzero satisfaction");
							}
						}
						if (leaders != 1) {
							throw bad(groupPath, "expected exactly one leader");
						}
					}
				}
			}

			void prepare(com.google.gson.JsonObject[] roots) {
				var restaurants = index(roots[0], names[0]);
				var unlocks = index(roots[1], names[1]);
				if (!restaurants.keySet().equals(unlocks.keySet())) {
					throw bad("restaurants", "ID sets differ between restaurant and unlock files");
				}
				maximumId = 0;
				openRestaurantIds.clear();
				supply(roots[0], "last_stats_reset_day", new com.google.gson.JsonPrimitive(day), names[0]);
				for (var entry : restaurants.entrySet()) {
					int id = entry.getKey();
					maximumId = Math.max(maximumId, id);
					migrateRestaurant(entry.getValue(), names[0] + ".restaurant(ID=" + id + ")");
					migrateUnlocks(unlocks.get(id), names[1] + ".restaurant(ID=" + id + ")");
				}
				migrateClients(roots[2], restaurants);
				validate("restaurants_file", roots[0], names[0], 0);
				validate("unlocks_file", roots[1], names[1], 0);
				validate("clients_file", roots[2], names[2], 0);
				statisticsDay = integer(roots[0], "last_stats_reset_day", names[0], -1, Long.MAX_VALUE);
				clientsDay = integer(roots[2], "database_day", names[2], -1, Long.MAX_VALUE);
				for (var entry : restaurants.entrySet()) {
					var restaurant = entry.getValue();
					String path = names[0] + ".restaurant(ID=" + entry.getKey() + ")";
					if (bool(restaurant, "open", path)) {
						openRestaurantIds.add((double) entry.getKey());
					}
					if (restaurant.has("last_settled_shift_id") != restaurant.has("last_shift_result")) {
						throw bad(path, "incomplete shift settlement record");
					}
					if (restaurant.has("last_shift_result")) {
						var result = object(restaurant.get("last_shift_result"), path + ".last_shift_result");
						if (!bool(result, "ready", path + ".last_shift_result") || integer(result, "shift_id", path + ".last_shift_result", 0, Long.MAX_VALUE) != integer(restaurant, "last_settled_shift_id", path, 0, Long.MAX_VALUE)) {
							throw bad(path, "shift result does not match its settlement marker");
						}
					}
				}
				checkClients(roots[2]);
				var ordered = new com.google.gson.JsonArray();
				for (int id : restaurants.keySet()) {
					ordered.add(unlocks.get(id));
				}
				roots[1].add("restaurants", ordered);
			}

			void durableWrite(java.nio.file.Path file, String content) throws java.io.IOException {
				byte[] bytes = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
				try (java.nio.channels.FileChannel channel = java.nio.channels.FileChannel.open(file, java.nio.file.StandardOpenOption.CREATE_NEW, java.nio.file.StandardOpenOption.WRITE)) {
					java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(bytes);
					while (buffer.hasRemaining()) {
						channel.write(buffer);
					}
					channel.force(true);
				}
			}

			void replace(java.nio.file.Path source, java.nio.file.Path target) throws java.io.IOException {
				java.nio.file.Files.move(source, target, java.nio.file.StandardCopyOption.ATOMIC_MOVE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}

			void cleanup(java.nio.file.Path folder) throws java.io.IOException {
				if (!java.nio.file.Files.exists(folder))
					return;
				try (var files = java.nio.file.Files.walk(folder)) {
					for (java.nio.file.Path file : files.sorted(java.util.Comparator.reverseOrder()).toList()) {
						java.nio.file.Files.delete(file);
					}
				}
			}

			void rollback(java.nio.file.Path transaction) throws java.io.IOException {
				java.nio.file.Path manifestPath = transaction.resolve("manifest.json");
				if (!java.nio.file.Files.exists(manifestPath)) {
					cleanup(transaction);
					return;
				}
				com.google.gson.JsonObject manifest = read(manifestPath);
				com.google.gson.JsonArray files = array(manifest, "files", "transaction");
				if (files.size() != names.length) {
					throw bad("transaction", "invalid file count");
				}
				for (int index = 0; index < files.size(); index++) {
					com.google.gson.JsonObject item = object(files.get(index), "transaction.file");
					if (!text(item, "name", "transaction.file", false).equals(names[index])) {
						throw bad("transaction", "filenames differ");
					}
					if (bool(item, "existed", "transaction.file") && !java.nio.file.Files.isRegularFile(transaction.resolve(index + ".before"))) {
						throw bad("transaction", "rollback data missing");
					}
				}
				for (int index = 0; index < files.size(); index++) {
					java.nio.file.Path target = directory.resolve(names[index]);
					if (files.get(index).getAsJsonObject().get("existed").getAsBoolean()) {
						java.nio.file.Path temporary = java.nio.file.Files.createTempFile(directory, ".rollback-", ".tmp");
						try {
							java.nio.file.Files.copy(transaction.resolve(index + ".before"), temporary, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
							replace(temporary, target);
						} finally {
							java.nio.file.Files.deleteIfExists(temporary);
						}
					} else {
						java.nio.file.Files.deleteIfExists(target);
					}
				}
				cleanup(transaction);
				log.accept("Rolled back an interrupted startup data transaction");
			}

			void recoverInterruptedCommit() throws java.io.IOException {
				java.nio.file.Path transaction = directory.resolve(".startup-transaction");
				if (!java.nio.file.Files.exists(transaction))
					return;
				if (java.nio.file.Files.exists(transaction.resolve("COMMITTED"))) {
					cleanup(transaction);
				} else {
					rollback(transaction);
				}
			}

			void commit(com.google.gson.JsonObject[] roots) throws java.io.IOException {
				java.util.List<Integer> changed = new java.util.ArrayList<>();
				for (int index = 0; index < 3; index++) {
					java.nio.file.Path target = directory.resolve(names[index]);
					boolean same = false;
					try {
						same = java.nio.file.Files.isRegularFile(target) && roots[index].equals(read(target));
					} catch (Exception ignored) {
						// The startup backup preserves the original.
					}
					if (!same) {
						changed.add(index);
					}
				}
				if (changed.isEmpty())
					return;
				java.nio.file.Path transaction = directory.resolve(".startup-transaction");
				java.nio.file.Files.createDirectory(transaction);
				boolean committed = false;
				try {
					com.google.gson.JsonObject manifest = new com.google.gson.JsonObject();
					com.google.gson.JsonArray files = new com.google.gson.JsonArray();
					com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
					for (int index = 0; index < 3; index++) {
						java.nio.file.Path target = directory.resolve(names[index]);
						boolean existed = java.nio.file.Files.exists(target);
						if (existed) {
							java.nio.file.Files.copy(target, transaction.resolve(index + ".before"));
						}
						com.google.gson.JsonObject item = new com.google.gson.JsonObject();
						item.addProperty("name", names[index]);
						item.addProperty("existed", existed);
						files.add(item);
						durableWrite(transaction.resolve(index + ".after"), gson.toJson(roots[index]));
						if (!roots[index].equals(read(transaction.resolve(index + ".after")))) {
							throw bad(names[index], "staged write verification failed");
						}
					}
					manifest.add("files", files);
					durableWrite(transaction.resolve("manifest.tmp"), gson.toJson(manifest));
					replace(transaction.resolve("manifest.tmp"), transaction.resolve("manifest.json"));
					for (int index : changed) {
						replace(transaction.resolve(index + ".after"), directory.resolve(names[index]));
					}
					for (int index = 0; index < 3; index++) {
						if (!roots[index].equals(read(directory.resolve(names[index])))) {
							throw bad(names[index], "final write verification failed");
						}
					}
					durableWrite(transaction.resolve("COMMITTED"), "complete\n");
					committed = true;
				} catch (java.io.IOException | RuntimeException exception) {
					try {
						rollback(transaction);
					} catch (Exception rollbackFailure) {
						exception.addSuppressed(rollbackFailure);
					}
					throw exception;
				} finally {
					if (committed) {
						try {
							cleanup(transaction);
						} catch (java.io.IOException exception) {
							log.accept("Committed data; journal cleanup deferred: " + exception.getMessage());
						}
					}
				}
			}

			public int run() throws java.io.IOException {
				java.nio.file.Files.createDirectories(directory);
				recoverInterruptedCommit();
				com.google.gson.JsonObject[] roots;
				try {
					roots = load(directory);
					prepare(roots);
				} catch (java.io.IOException | RuntimeException originalFailure) {
					roots = null;
					java.nio.file.Path backups = directory.resolve("backups");
					if (java.nio.file.Files.isDirectory(backups)) {
						java.util.List<java.nio.file.Path> candidates;
						try (var files = java.nio.file.Files.list(backups)) {
							candidates = files.filter(java.nio.file.Files::isDirectory).filter(file -> file.getFileName().toString().startsWith("startup-"))
									.sorted(java.util.Comparator.comparing((java.nio.file.Path path) -> path.getFileName().toString()).reversed()).toList();
						}
						for (java.nio.file.Path candidate : candidates) {
							if (!java.nio.file.Files.isRegularFile(candidate.resolve(names[0]))) {
								continue;
							}
							try {
								com.google.gson.JsonObject[] restored = load(candidate);
								prepare(restored);
								roots = restored;
								log.accept("RESTORING ALL THREE FILES from " + candidate + "; current dataset failed: " + originalFailure.getMessage());
								break;
							} catch (java.io.IOException | RuntimeException ignored) {
								// Try the next complete, valid backup.
							}
						}
					}
					if (roots == null) {
						throw originalFailure;
					}
				}
				commit(roots);
				return maximumId + 1;
			}
		}
		try {
			if (world instanceof net.minecraft.server.level.ServerLevel level && level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
				if (folder.isBlank()) {
					throw new java.io.IOException("World data path is empty");
				}
				var variables = net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world);
				String[] fileNames = {variables.Restaurant_File_Name, variables.RestaurantFood_File_Name, variables.ClientsDatabase_File_Name};
				var engine = new StartupDataEngine(java.nio.file.Path.of(folder), fileNames, Math.floorDiv(level.getDayTime(), 24000L), new com.google.gson.JsonObject(), CreateModDataSchemasProcedure.execute(),
						message -> net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef startup: " + message));
				engine.allowFreshDataset = variables.RestaurantID <= 1;
				engine.recoverInterruptedCommit();
				variables.Restaurant_Info_Path = folder;
				variables.markSyncDirty();
				CreatingFoodListJsonProcedure.execute(world);
				CreatingClientsNameDatabaseJsonFileProcedure.execute(world);
				var config = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef");
				var foodDatabase = engine.read(config.resolve(variables.FoodDatabase_File_Name));
				engine.baseExp = (amounts, foods) -> GetClientOrderBaseExpFromArraysProcedure.execute(amounts, foods, foodDatabase);
				engine.integer(foodDatabase, "tier_count", "FoodDatabase", 1, Integer.MAX_VALUE);
				engine.catalog.add("tier_count", foodDatabase.get("tier_count"));
				for (String groupKey : java.util.List.of("ingredients", "foods")) {
					String groupPath = "FoodDatabase." + groupKey;
					var group = engine.object(foodDatabase.get(groupKey), groupPath);
					long groupCount = engine.integer(group, "tier_count", groupPath, 1, Integer.MAX_VALUE);
					var tiers = engine.object(group.get("tiers"), groupPath + ".tiers");
					for (int tier = 0; tier < groupCount; tier++) {
						engine.array(tiers, Integer.toString(tier), groupPath + ".tiers");
					}
					engine.catalog.add(groupKey, group);
				}
				var nameDatabase = engine.read(config.resolve("ClientsNameList.json"));
				engine.strings(nameDatabase, "Clients names", "ClientsNameList", 0);
				engine.strings(nameDatabase, "Critics names", "ClientsNameList", 0);
				if (Double.isFinite(variables.RestaurantID) && variables.RestaurantID > Integer.MAX_VALUE) {
					throw new IllegalStateException("Restaurant ID counter exceeds supported integer range");
				}
				int nextId = engine.run();
				if (!Double.isFinite(variables.RestaurantID) || variables.RestaurantID < nextId || variables.RestaurantID != Math.rint(variables.RestaurantID)) {
					variables.RestaurantID = Math.max(nextId, Double.isFinite(variables.RestaurantID) ? Math.ceil(variables.RestaurantID) : nextId);
					variables.markSyncDirty();
				}
				variables.RestaurantsOpen.clear();
				variables.RestaurantsOpen.addAll(engine.openRestaurantIds);
				variables.LastClientsDatabaseResetDay = Math.min(engine.day, Math.min(engine.statisticsDay, engine.clientsDay));
				variables.markSyncDirty();
				success = true;
				net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef: startup data initialization completed.");
			}
		} catch (Exception exception) {
			net.mcreator.omnichef.OmnichefMod.LOGGER.error("Omnichef: startup data initialization failed; restaurant data remains unavailable.", exception);
		}
		return success;
	}
}