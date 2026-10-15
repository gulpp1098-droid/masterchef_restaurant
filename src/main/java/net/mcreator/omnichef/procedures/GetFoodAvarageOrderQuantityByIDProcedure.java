package net.mcreator.omnichef.procedures;

public class GetFoodAvarageOrderQuantityByIDProcedure {
	public static double execute(com.google.gson.JsonObject databaseObject, double maxStackSize, String foodID) {
		if (databaseObject == null || foodID == null)
			return 0;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		double foodScore = 0;
		double targetPortionValue = 0;
		double maxQuantity = 0;
		double quantity = 0;
		double totalQuantity = 0;
		database = databaseObject;
		foodScore = GetFoodScoreByIDProcedure.execute(database, foodID);
		targetPortionValue = 1;
		maxQuantity = Math.min(maxStackSize, 8);
		quantity = Math.ceil(targetPortionValue / Math.max(1, foodScore));
		quantity = Math.min(maxQuantity, Math.max(1, quantity));
		for (int _i1 = 0; _i1 < 50; _i1++) {
			quantity = Math.ceil(targetPortionValue / Math.max(1, foodScore));
			quantity = Math.min(maxQuantity, Math.max(1, quantity));
			totalQuantity = totalQuantity + quantity;
			targetPortionValue = targetPortionValue + 1;
		}
		return totalQuantity / 50;
	}
}