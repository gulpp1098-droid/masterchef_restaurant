package net.mcreator.masterchefrestaurant.procedures;

public class CreateFoodCategoryBonusMapProcedure {
	public static com.google.gson.JsonObject execute() {
		com.google.gson.JsonObject specialBonuses = new com.google.gson.JsonObject();
		specialBonuses.addProperty("c:foods/raw_fish", 3);
		specialBonuses.addProperty("c:foods/berry", 2);
		specialBonuses.addProperty("c:foods/raw_meat", 1);
		specialBonuses.addProperty("c:foods/food_poisoning", (-1));
		specialBonuses.addProperty("c:foods/cooked_fish", 0);
		specialBonuses.addProperty("c:foods/cooked_meat", 0);
		specialBonuses.addProperty("c:foods/fruit", 0);
		specialBonuses.addProperty("c:foods/vegetable", 0);
		specialBonuses.addProperty("c:foods/bread", 0);
		specialBonuses.addProperty("c:foods/cookie", 0);
		specialBonuses.addProperty("c:foods/soup", 0);
		specialBonuses.addProperty("c:foods/golden", 0);
		specialBonuses.addProperty("c:foods/edible_when_placed", 0);
		return specialBonuses;
	}
}