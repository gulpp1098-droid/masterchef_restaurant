package net.mcreator.omnichef.procedures;

public class CreateDefaultRestaurantStatsProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject stats = new com.google.gson.JsonObject();
		stats.addProperty("customers_served_fully", 0);
		stats.addProperty("customers_served", 0);
		stats.addProperty("customers_lost", 0);
		stats.addProperty("coins_earned", 0);
		stats.addProperty("reputation_change", 0);
		return stats;
	}
}