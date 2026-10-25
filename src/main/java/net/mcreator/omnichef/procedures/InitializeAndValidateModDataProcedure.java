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
			final java.util.function.Consumer<String> log;
			int maximumId;
			boolean allowFreshDataset = true;
			final java.util.List<Double> openRestaurantIds = new java.util.ArrayList<>();
			long statisticsDay;
			long clientsDay;

			public StartupDataEngine(java.nio.file.Path directory, String[] names, long day, com.google.gson.JsonObject catalog, java.util.function.Consumer<String> log) throws java.io.IOException {
				this.directory = directory;
				this.names = names.clone();
				this.day = day;
				this.catalog = catalog;
				this.log = log;
				java.util.Set<String> unique = new java.util.HashSet<>();
				for (String name : names) {
					if (name.isBlank() || !java.nio.file.Path.of(name).getFileName().toString().equals(name) || name.contains("\\") || name.equals(".") || name.equals("..") || !unique.add(name))
						throw new java.io.IOException("Invalid or duplicate data filename: " + name);
				}
				if (names.length != 3)
					throw new java.io.IOException("Expected three data filenames");
			}

			IllegalStateException bad(String path, String reason) {
				return new IllegalStateException(path + ": " + reason);
			}

			// Read strict JSON, reject duplicate keys instead of silently losing one value.
			com.google.gson.JsonElement readValue(com.google.gson.stream.JsonReader reader, String path, int depth) throws java.io.IOException {
				if (depth > 128)
					throw bad(path, "JSON nesting exceeds 128");
				switch (reader.peek()) {
					case BEGIN_OBJECT :
						com.google.gson.JsonObject object = new com.google.gson.JsonObject();
						reader.beginObject();
						while (reader.hasNext()) {
							String key = reader.nextName();
							if (object.has(key))
								throw bad(path + "." + key, "duplicate JSON key");
							object.add(key, readValue(reader, path + "." + key, depth + 1));
						}
						reader.endObject();
						return object;
					case BEGIN_ARRAY :
						com.google.gson.JsonArray array = new com.google.gson.JsonArray();
						reader.beginArray();
						while (reader.hasNext())
							array.add(readValue(reader, path + "[" + array.size() + "]", depth + 1));
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
					if (reader.peek() != com.google.gson.stream.JsonToken.END_DOCUMENT)
						throw bad(file.toString(), "trailing JSON data");
					return object(value, file.getFileName().toString());
				}
			}

			com.google.gson.JsonObject object(com.google.gson.JsonElement value, String path) {
				if (value == null || !value.isJsonObject())
					throw bad(path, "required object missing or wrong type");
				return value.getAsJsonObject();
			}

			com.google.gson.JsonArray array(com.google.gson.JsonObject object, String key, String path) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonArray())
					throw bad(path + "." + key, "required array missing or wrong type");
				return value.getAsJsonArray();
			}

			String text(com.google.gson.JsonObject object, String key, String path, boolean emptyAllowed) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString())
					throw bad(path + "." + key, "required string missing or wrong type");
				String result = value.getAsString();
				if (!emptyAllowed && result.isBlank())
					throw bad(path + "." + key, "empty string");
				return result;
			}

			double number(com.google.gson.JsonObject object, String key, String path) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber() || !Double.isFinite(value.getAsDouble()))
					throw bad(path + "." + key, "required finite number missing or wrong type");
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
				if (value < minimum || value > maximum)
					throw bad(path + "." + key, "integer out of range");
				return value;
			}

			boolean bool(com.google.gson.JsonObject object, String key, String path) {
				com.google.gson.JsonElement value = object.get(key);
				if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean())
					throw bad(path + "." + key, "required boolean missing or wrong type");
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
					if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || value.getAsString().isBlank())
						throw bad(location, "expected nonempty string");
					if (coordinateParts > 0)
						coordinates(value.getAsString(), coordinateParts, location);
				}
			}

			void coordinates(String value, int parts, String path) {
				String[] coordinates = value.split(":", -1);
				if (coordinates.length != parts)
					throw bad(path, "invalid coordinate count");
				for (String coordinate : coordinates) {
					try {
						// MCreator concatenates doubles (12.0); queue endpoints use block centres (12.5).
						java.math.BigDecimal numeric = new java.math.BigDecimal(coordinate.trim());
						if (numeric.compareTo(java.math.BigDecimal.valueOf(Integer.MIN_VALUE)) < 0 || numeric.compareTo(java.math.BigDecimal.valueOf(Integer.MAX_VALUE)) > 0)
							throw bad(path, "coordinate out of range");
						// Claimed locations are tile indices; physical positions may include fractions.
						if (parts == 2)
							numeric.intValueExact();
					} catch (NumberFormatException | ArithmeticException exception) {
						throw bad(path, "invalid coordinate: " + coordinate);
					}
				}
			}

			void statistics(com.google.gson.JsonObject restaurant, String key, String path) {
				supply(restaurant, key, new com.google.gson.JsonObject(), path);
				com.google.gson.JsonObject stats = object(restaurant.get(key), path + "." + key);
				for (String field : new String[]{"customers_served_fully", "customers_served", "customers_lost", "coins_earned", "reputation_change"}) {
					supply(stats, field, new com.google.gson.JsonPrimitive(0), path + "." + key);
					double value = number(stats, field, path + "." + key);
					if (!field.equals("reputation_change") && value < 0)
						throw bad(path + "." + key + "." + field, "negative statistic");
				}
			}

			java.util.Map<Integer, com.google.gson.JsonObject> index(com.google.gson.JsonObject root, String file) {
				java.util.Map<Integer, com.google.gson.JsonObject> result = new java.util.LinkedHashMap<>();
				com.google.gson.JsonArray records = array(root, "restaurants", file);
				for (int index = 0; index < records.size(); index++) {
					String path = file + ".restaurants[" + index + "]";
					com.google.gson.JsonObject record = object(records.get(index), path);
					int id = (int) integer(record, "ID", path, 1, Integer.MAX_VALUE - 1);
					if (result.put(id, record) != null)
						throw bad(path + ".ID", "duplicate restaurant ID " + id);
				}
				return result;
			}

			com.google.gson.JsonObject emptyRoot(String dayField) {
				com.google.gson.JsonObject root = new com.google.gson.JsonObject();
				root.add("restaurants", new com.google.gson.JsonArray());
				if (dayField != null)
					root.addProperty(dayField, day);
				return root;
			}

			com.google.gson.JsonObject[] load(java.nio.file.Path source) throws java.io.IOException {
				boolean anyExists = false;
				for (String name : names)
					anyExists |= java.nio.file.Files.exists(source.resolve(name));
				if (!anyExists) {
					if (source.equals(directory) && (!allowFreshDataset || java.nio.file.Files.isDirectory(source.resolve("backups"))))
						throw bad("dataset", "all files missing from an existing world");
					return new com.google.gson.JsonObject[]{emptyRoot("last_stats_reset_day"), emptyRoot(null), emptyRoot("database_day")};
				}
				com.google.gson.JsonObject[] roots = new com.google.gson.JsonObject[3];
				for (int index = 0; index < 3; index++) {
					java.nio.file.Path file = source.resolve(names[index]);
					if (java.nio.file.Files.isRegularFile(file))
						roots[index] = read(file);
				}
				// Missing restaurant/progress records cannot be distinguished from lost progress.
				if (roots[0] == null)
					throw bad(names[0], "file missing from an existing dataset");
				if (roots[1] == null && array(roots[0], "restaurants", names[0]).isEmpty())
					roots[1] = emptyRoot(null);
				if (roots[2] == null && array(roots[0], "restaurants", names[0]).isEmpty())
					roots[2] = emptyRoot("database_day");
				for (int index = 0; index < 3; index++)
					if (roots[index] == null)
						throw bad(names[index], "file missing from an existing dataset");
				return roots;
			}

			void prepare(com.google.gson.JsonObject[] roots) {
				java.util.Map<Integer, com.google.gson.JsonObject> restaurants = index(roots[0], names[0]);
				java.util.Map<Integer, com.google.gson.JsonObject> unlocks = index(roots[1], names[1]);
				if (!restaurants.keySet().equals(unlocks.keySet()))
					throw bad("restaurants", "ID sets differ between restaurant and unlock files");
				maximumId = 0;
				openRestaurantIds.clear();
				supply(roots[0], "last_stats_reset_day", new com.google.gson.JsonPrimitive(day), names[0]);
				statisticsDay = integer(roots[0], "last_stats_reset_day", names[0], -1, Long.MAX_VALUE);
				for (var entry : restaurants.entrySet()) {
					int id = entry.getKey();
					maximumId = Math.max(maximumId, id);
					com.google.gson.JsonObject restaurant = entry.getValue();
					String path = names[0] + ".restaurant(ID=" + id + ")";
					text(restaurant, "name", path, false);
					String owner = text(restaurant, "owner", path, false);
					try {
						if (!java.util.UUID.fromString(owner).toString().equalsIgnoreCase(owner))
							throw bad(path + ".owner", "invalid java.util.UUID");
					} catch (IllegalArgumentException exception) {
						throw bad(path + ".owner", "invalid java.util.UUID");
					}
					supply(restaurant, "ownerName", new com.google.gson.JsonPrimitive(owner), path);
					text(restaurant, "ownerName", path, false);
					integer(restaurant, "level", path, 0, Integer.MAX_VALUE);
					number(restaurant, "reputation", path);
					supply(restaurant, "coowners", new com.google.gson.JsonArray(), path);
					strings(restaurant, "coowners", path, 0);
					strings(restaurant, "locations", path, 2);
					strings(restaurant, "tables", path, 3);
					String reception = text(restaurant, "reception", path, true);
					if (!reception.isEmpty())
						coordinates(reception, 3, path + ".reception");
					strings(restaurant, "menu", path, 0);
					strings(restaurant, "next_menu", path, 0);
					supply(restaurant, "open", new com.google.gson.JsonPrimitive(false), path);
					supply(restaurant, "close_time", new com.google.gson.JsonPrimitive(0), path);
					supply(restaurant, "last_day_open", new com.google.gson.JsonPrimitive(-1), path);
					supply(restaurant, "menu_advance_ready", new com.google.gson.JsonPrimitive(false), path);
					if (bool(restaurant, "open", path))
						openRestaurantIds.add((double) id);
					number(restaurant, "close_time", path);
					integer(restaurant, "active_groups", path, 0, Integer.MAX_VALUE);
					integer(restaurant, "last_day_open", path, -1, Long.MAX_VALUE);
					bool(restaurant, "menu_advance_ready", path);
					statistics(restaurant, "daily_stats", path);
					statistics(restaurant, "last_day_stats", path);
					supply(restaurant, "trade_board", new com.google.gson.JsonObject(), path);
					com.google.gson.JsonObject tradeBoard = object(restaurant.get("trade_board"), path + ".trade_board");
					boolean incompleteTradeBoard = !tradeBoard.has("day") || !tradeBoard.has("offers");
					supply(tradeBoard, "day", new com.google.gson.JsonPrimitive(-1), path + ".trade_board");
					supply(tradeBoard, "offers", new com.google.gson.JsonArray(), path + ".trade_board");
					integer(tradeBoard, "day", path + ".trade_board", -1, Long.MAX_VALUE);
					com.google.gson.JsonArray tradeOffers = array(tradeBoard, "offers", path + ".trade_board");
					if (incompleteTradeBoard) {
						tradeBoard.addProperty("day", -1);
						tradeBoard.add("offers", new com.google.gson.JsonArray());
						log.accept("Initialized " + path + ".trade_board");
					} else {
						if (tradeOffers.size() > 5)
							throw bad(path + ".trade_board.offers", "more than five offers");
						for (int offerIndex = 0; offerIndex < tradeOffers.size(); offerIndex++) {
							String offerPath = path + ".trade_board.offers[" + offerIndex + "]";
							com.google.gson.JsonObject offer = object(tradeOffers.get(offerIndex), offerPath);
							text(offer, "input_id", offerPath, false);
							text(offer, "output_id", offerPath, false);
							integer(offer, "input_count", offerPath, 1, Integer.MAX_VALUE);
							integer(offer, "output_count", offerPath, 1, Integer.MAX_VALUE);
							bool(offer, "used", offerPath);
						}
					}
					com.google.gson.JsonObject unlock = unlocks.get(id);
					path = names[1] + ".restaurant(ID=" + id + ")";
					if (number(unlock, "discovery_exp", path) < 0)
						throw bad(path, "negative discovery experience");
					int stage = (int) integer(unlock, "stage_tier", path, 0, Integer.MAX_VALUE);
					long starter = integer(unlock, "starter_unlocks_remaining", path, 0, 3);
					strings(unlock, "unlocked", path, 0);
					// Pending cards and earned choices are progress: preserve them or use a backup.
					com.google.gson.JsonArray cards = array(unlock, "pending_cards", path);
					strings(unlock, "pending_cards", path, 0);
					if (cards.size() > 3)
						throw bad(path + ".pending_cards", "more than three cards");
					supply(unlock, "pending_unlocks", new com.google.gson.JsonPrimitive(starter <= 0 && !cards.isEmpty() ? 1 : 0), path);
					integer(unlock, "pending_unlocks", path, 0, 5);
					if (!unlock.has("unlock_options")) {
						com.google.gson.JsonArray options = new com.google.gson.JsonArray();
						java.util.Set<String> unlocked = new java.util.HashSet<>();
						for (com.google.gson.JsonElement value : unlock.getAsJsonArray("unlocked"))
							unlocked.add(value.getAsString());
						com.google.gson.JsonObject tiers = object(catalog.get("tiers"), "FoodDatabase.tiers");
						long count = integer(catalog, "tier_count", "FoodDatabase", 1, Integer.MAX_VALUE);
						if (stage >= count)
							throw bad(path + ".stage_tier", "cannot reconstruct options: stage exceeds current food tiers");
						java.util.Set<String> seen = new java.util.HashSet<>();
						for (int tier = 0; tier <= stage; tier++) {
							for (com.google.gson.JsonElement value : array(tiers, Integer.toString(tier), "FoodDatabase.tiers")) {
								String food = text(object(value, "FoodDatabase.food"), "id", "FoodDatabase.food", false);
								if (!unlocked.contains(food) && seen.add(food))
									options.add(food);
							}
						}
						unlock.add("unlock_options", options);
						log.accept("Reconstructed " + path + ".unlock_options");
					}
					strings(unlock, "unlock_options", path, 0);
				}
				com.google.gson.JsonArray ordered = new com.google.gson.JsonArray();
				for (int id : restaurants.keySet())
					ordered.add(unlocks.get(id));
				roots[1].add("restaurants", ordered);
				validateClients(roots[2]);
			}

			void validateClients(com.google.gson.JsonObject root) {
				com.google.gson.JsonArray records = array(root, "restaurants", names[2]);
				if (records.isEmpty())
					supply(root, "database_day", new com.google.gson.JsonPrimitive(day), names[2]);
				clientsDay = integer(root, "database_day", names[2], -1, Long.MAX_VALUE);
				for (var entry : index(root, names[2]).entrySet()) {
					com.google.gson.JsonObject restaurant = entry.getValue();
					String path = names[2] + ".restaurant(ID=" + entry.getKey() + ")";
					number(restaurant, "next_spawn_group_time", path);
					integer(restaurant, "next_spawn_group_index", path, -1, Integer.MAX_VALUE);
					String queue = text(restaurant, "queue_start", path, true);
					if (!queue.isEmpty())
						coordinates(queue, 3, path + ".queue_start");
					java.util.Set<Long> groupIds = new java.util.HashSet<>();
					int groupIndex = 0;
					for (com.google.gson.JsonElement groupValue : array(restaurant, "groups", path)) {
						String groupPath = path + ".groups[" + groupIndex++ + "]";
						com.google.gson.JsonObject group = object(groupValue, groupPath);
						long groupId = integer(group, "group_id", groupPath, 0, Integer.MAX_VALUE);
						if (!groupIds.add(groupId))
							throw bad(groupPath, "duplicate group ID");
						number(group, "spawn_time", groupPath);
						bool(group, "spawned", groupPath);
						text(group, "state", groupPath, false);
						int memberIndex = 0;
						int leaders = 0;
						for (com.google.gson.JsonElement memberValue : array(group, "members", groupPath)) {
							String memberPath = groupPath + ".members[" + memberIndex++ + "]";
							com.google.gson.JsonObject member = object(memberValue, memberPath);
							if (bool(member, "leader", memberPath))
								leaders++;
							bool(member, "critic", memberPath);
							number(member, "patience", memberPath);
							strings(member, "food", memberPath, 0);
							com.google.gson.JsonArray amounts = array(member, "food_amount", memberPath);
							if (amounts.size() != member.getAsJsonArray("food").size())
								throw bad(memberPath, "food and food_amount lengths differ");
							for (int index = 0; index < amounts.size(); index++) {
								com.google.gson.JsonObject box = new com.google.gson.JsonObject();
								box.add("amount", amounts.get(index));
								integer(box, "amount", memberPath + ".food_amount[" + index + "]", 1, Integer.MAX_VALUE);
							}
						}
						if (leaders != 1)
							throw bad(groupPath, "expected exactly one leader");
					}
				}
			}

			void durableWrite(java.nio.file.Path file, String content) throws java.io.IOException {
				byte[] bytes = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
				try (java.nio.channels.FileChannel channel = java.nio.channels.FileChannel.open(file, java.nio.file.StandardOpenOption.CREATE_NEW, java.nio.file.StandardOpenOption.WRITE)) {
					java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(bytes);
					while (buffer.hasRemaining())
						channel.write(buffer);
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
					for (java.nio.file.Path file : files.sorted(java.util.Comparator.reverseOrder()).toList())
						java.nio.file.Files.delete(file);
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
				if (files.size() != names.length)
					throw bad("transaction", "invalid file count");
				// Validate the complete journal before touching any originals.
				for (int index = 0; index < files.size(); index++) {
					com.google.gson.JsonObject item = object(files.get(index), "transaction.file");
					if (!text(item, "name", "transaction.file", false).equals(names[index]))
						throw bad("transaction", "filenames differ");
					if (bool(item, "existed", "transaction.file") && !java.nio.file.Files.isRegularFile(transaction.resolve(index + ".before")))
						throw bad("transaction", "rollback data missing");
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
					} else
						java.nio.file.Files.deleteIfExists(target);
				}
				cleanup(transaction);
				log.accept("Rolled back an interrupted startup data transaction");
			}

			void recoverInterruptedCommit() throws java.io.IOException {
				java.nio.file.Path transaction = directory.resolve(".startup-transaction");
				if (!java.nio.file.Files.exists(transaction))
					return;
				if (java.nio.file.Files.exists(transaction.resolve("COMMITTED")))
					cleanup(transaction);
				else
					rollback(transaction);
			}

			void commit(com.google.gson.JsonObject[] roots) throws java.io.IOException {
				java.util.List<Integer> changed = new java.util.ArrayList<>();
				for (int index = 0; index < 3; index++) {
					java.nio.file.Path target = directory.resolve(names[index]);
					boolean same = false;
					try {
						same = java.nio.file.Files.isRegularFile(target) && roots[index].equals(read(target));
					} catch (Exception ignored) {
						/* Invalid original was preserved by startup backup. */ }
					if (!same)
						changed.add(index);
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
						if (existed)
							java.nio.file.Files.copy(target, transaction.resolve(index + ".before"));
						com.google.gson.JsonObject item = new com.google.gson.JsonObject();
						item.addProperty("name", names[index]);
						item.addProperty("existed", existed);
						files.add(item);
						durableWrite(transaction.resolve(index + ".after"), gson.toJson(roots[index]));
						if (!roots[index].equals(read(transaction.resolve(index + ".after"))))
							throw bad(names[index], "staged write verification failed");
					}
					manifest.add("files", files);
					durableWrite(transaction.resolve("manifest.tmp"), gson.toJson(manifest));
					replace(transaction.resolve("manifest.tmp"), transaction.resolve("manifest.json"));
					for (int index : changed)
						replace(transaction.resolve(index + ".after"), directory.resolve(names[index]));
					for (int index = 0; index < 3; index++)
						if (!roots[index].equals(read(directory.resolve(names[index]))))
							throw bad(names[index], "final write verification failed");
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
							// A missing primary document is never evidence of a new empty world in a backup.
							if (!java.nio.file.Files.isRegularFile(candidate.resolve(names[0])))
								continue;
							try {
								com.google.gson.JsonObject[] restored = load(candidate);
								prepare(restored);
								roots = restored;
								log.accept("RESTORING ALL THREE FILES from " + candidate + "; current dataset failed: " + originalFailure.getMessage());
								break;
							} catch (java.io.IOException | RuntimeException ignored) {
								/* Try the next complete, valid dataset. */ }
						}
					}
					if (roots == null)
						throw originalFailure;
				}
				commit(roots);
				return maximumId + 1;
			}
		}
		try {
			if (world instanceof net.minecraft.server.level.ServerLevel level && level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
				if (folder.isBlank())
					throw new java.io.IOException("World data path is empty");
				var variables = net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world);
				String[] fileNames = {variables.Restaurant_File_Name, variables.RestaurantFood_File_Name, variables.ClientsDatabase_File_Name};
				var engine = new StartupDataEngine(java.nio.file.Path.of(folder), fileNames, Math.floorDiv(level.getDayTime(), 24000L), new com.google.gson.JsonObject(),
						message -> net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef startup: " + message));
				engine.allowFreshDataset = variables.RestaurantID <= 1;
				engine.recoverInterruptedCommit();
				variables.Restaurant_Info_Path = folder;
				variables.markSyncDirty();
				CreatingFoodListJsonProcedure.execute(world);
				CreatingClientsNameDatabaseJsonFileProcedure.execute(world);
				var config = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef");
				var foodDatabase = engine.read(config.resolve(variables.FoodDatabase_File_Name));
				engine.integer(foodDatabase, "tier_count", "FoodDatabase", 1, Integer.MAX_VALUE);
				engine.object(foodDatabase.get("tiers"), "FoodDatabase.tiers");
				engine.catalog.add("tier_count", foodDatabase.get("tier_count"));
				engine.catalog.add("tiers", foodDatabase.get("tiers"));
				var nameDatabase = engine.read(config.resolve("ClientsNameList.json"));
				engine.strings(nameDatabase, "Clients names", "ClientsNameList", 0);
				engine.strings(nameDatabase, "Critics names", "ClientsNameList", 0);
				if (Double.isFinite(variables.RestaurantID) && variables.RestaurantID > Integer.MAX_VALUE)
					throw new IllegalStateException("Restaurant ID counter exceeds supported integer range");
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