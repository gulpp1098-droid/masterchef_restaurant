package net.mcreator.omnichef.procedures;

public class CreateDefaultRestaurantFieldsProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject defaults = new com.google.gson.JsonObject();
		com.google.gson.JsonArray emptyArray = new com.google.gson.JsonArray();
		defaults.add("coowners", emptyArray);
		defaults.addProperty("open", false);
		defaults.addProperty("close_time", 0);
		defaults.addProperty("last_day_open", (-1));
		defaults.addProperty("menu_advance_ready", false);
		return defaults;
	}
}