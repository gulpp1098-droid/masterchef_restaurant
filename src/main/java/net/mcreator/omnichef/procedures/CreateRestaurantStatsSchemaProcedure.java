package net.mcreator.omnichef.procedures;

public class CreateRestaurantStatsSchemaProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject schema = new com.google.gson.JsonObject();
		com.google.gson.JsonObject defaultStats = new com.google.gson.JsonObject();
		com.google.gson.JsonArray negativeAllowed = new com.google.gson.JsonArray();
		negativeAllowed.add("reputation_change");
		defaultStats = CreateDefaultRestaurantStatsProcedure.execute();
		schema.add("defaults", defaultStats);
		schema.add("negative_allowed", negativeAllowed);
		return schema;
	}
}