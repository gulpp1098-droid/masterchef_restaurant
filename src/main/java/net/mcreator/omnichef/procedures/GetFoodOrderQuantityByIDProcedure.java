package net.mcreator.omnichef.procedures;

import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class GetFoodOrderQuantityByIDProcedure {
	public static double execute(com.google.gson.JsonObject databaseObject, double maxStackSize, String foodID) {
		if (databaseObject == null || foodID == null)
			return 0;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		double foodScore = 0;
		double targetPortionValue = 0;
		double maxQuantity = 0;
		double quantity = 0;
		database = databaseObject;
		foodScore = GetFoodScoreByIDProcedure.execute(database, foodID);
		targetPortionValue = Mth.nextInt(RandomSource.create(), 1, 50);
		maxQuantity = Math.min(maxStackSize, 8);
		quantity = Math.ceil(targetPortionValue / Math.max(1, foodScore));
		quantity = Math.min(maxQuantity, Math.max(1, quantity));
		return quantity;
	}
}