package net.mcreator.omnichef.procedures;

public class MergeFoodAmountsByDiscoveryProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonArray foodAmountArray, com.google.gson.JsonArray foodArray, com.google.gson.JsonObject databaseObject) {
		if (foodAmountArray == null || foodArray == null || databaseObject == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject result = new com.google.gson.JsonObject();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray mergedFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray mergedAmounts = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodArr = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArr = new com.google.gson.JsonArray();
		String foodID = "";
		double index = 0;
		double mergedIndex = 0;
		double amount = 0;
		boolean found = false;
		database = databaseObject;
		foodArr = foodArray;
		foodAmountArr = foodAmountArray;
		for (int _i1 = 0; _i1 < (int) Math.min(foodArr.size(), foodAmountArr.size()); _i1++) {
			foodID = GetFoodDiscoveryIDProcedure.execute(database, foodArr.get((int) index).getAsString());
			amount = foodAmountArr.get((int) index).getAsDouble();
			mergedIndex = 0;
			found = false;
			for (int _i2 = 0; _i2 < (int) mergedFoods.size(); _i2++) {
				if ((mergedFoods.get((int) mergedIndex).getAsString()).equals(foodID)) {
					amount = amount + mergedAmounts.get((int) mergedIndex).getAsDouble();
					mergedFoods.remove(((int) mergedIndex));
					mergedAmounts.remove(((int) mergedIndex));
					mergedFoods.add(foodID);
					mergedAmounts.add(amount);
					found = true;
					break;
				}
				mergedIndex = mergedIndex + 1;
			}
			if (!found) {
				mergedFoods.add(foodID);
				mergedAmounts.add(amount);
			}
			index = index + 1;
		}
		result.add("food", mergedFoods);
		result.add("food_amount", mergedAmounts);
		return result;
	}
}