package net.mcreator.omnichef.procedures;

public class CreateSpecialFoodBonusMapProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject specialBonuses = new com.google.gson.JsonObject();
		specialBonuses.addProperty("minecraft:tropical_fish", 6);
		specialBonuses.addProperty("minecraft:glow_berries", 4);
		specialBonuses.addProperty("minecraft:chorus_fruit", 8);
		return specialBonuses;
	}
}