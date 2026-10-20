package net.mcreator.omnichef.procedures;

public class CreateDefaultTradeBoardDataProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject tradeBoard = new com.google.gson.JsonObject();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		tradeBoard.addProperty("day", (-1));
		tradeBoard.add("offers", offers);
		return tradeBoard;
	}
}