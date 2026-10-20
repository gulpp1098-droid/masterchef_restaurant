package net.mcreator.omnichef.procedures;

import net.mcreator.omnichef.OmnichefMod;

public class GenerateTradeBoardOffersProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray unlockedArray, com.google.gson.JsonObject databaseObject) {
		if (unlockedArray == null || databaseObject == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonArray pool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray candidates = new com.google.gson.JsonArray();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		com.google.gson.JsonObject pair = new com.google.gson.JsonObject();
		com.google.gson.JsonObject amounts = new com.google.gson.JsonObject();
		double inputIndex = 0;
		double outputIndex = 0;
		double randomIndex = 0;
		pool = GetTradeBoardIngredientPoolProcedure.execute(unlockedArray, databaseObject);
		if (pool.size() < 2) {
			return offers;
		}
		for (int _i1 = 0; _i1 < (int) pool.size(); _i1++) {
			outputIndex = 0;
			for (int _i2 = 0; _i2 < (int) pool.size(); _i2++) {
				if (!(inputIndex == outputIndex)) {
					pair = new Object() {
						public com.google.gson.JsonObject parse(String rawJson) {
							try {
								return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
							} catch (Exception e) {
								OmnichefMod.LOGGER.error(e);
								return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
							}
						}
					}.parse("{}");
					pair.addProperty("input_id", pool.get((int) inputIndex).getAsString());
					pair.addProperty("output_id", pool.get((int) outputIndex).getAsString());
					candidates.add(pair);
				}
				outputIndex = outputIndex + 1;
			}
			inputIndex = inputIndex + 1;
		}
		while (offers.size() < 5 && candidates.size() > 0) {
			randomIndex = Math.floor(Math.random() * candidates.size());
			pair = candidates.get((int) randomIndex).getAsJsonObject();
			candidates.remove(((int) randomIndex));
			amounts = CalculateTradeBoardAmountsProcedure.execute(databaseObject, pair.get("input_id").getAsString(), pair.get("output_id").getAsString());
			if (amounts.has("input_count")) {
				pair.addProperty("input_count", amounts.get("input_count").getAsDouble());
				pair.addProperty("output_count", amounts.get("output_count").getAsDouble());
				pair.addProperty("used", false);
				offers.add(pair);
			}
		}
		return offers;
	}
}