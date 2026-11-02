package net.mcreator.omnichef.procedures;

public class CreateTradeOfferSchemaProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject schema = new com.google.gson.JsonObject();
		com.google.gson.JsonObject required = new com.google.gson.JsonObject();
		com.google.gson.JsonObject defaults = new com.google.gson.JsonObject();
		required.addProperty("input_id", "nonempty_string");
		required.addProperty("output_id", "nonempty_string");
		required.addProperty("input_count", "positive_integer");
		required.addProperty("output_count", "positive_integer");
		required.addProperty("used", "boolean");
		schema.add("required", required);
		schema.add("defaults", defaults);
		return schema;
	}
}