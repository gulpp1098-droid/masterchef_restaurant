package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public class GetClientOrderBaseExpFromArraysProcedure {
	public static double execute(com.google.gson.JsonArray orderAmounts, com.google.gson.JsonArray orderFoods, com.google.gson.JsonObject databaseObject) {
		if (orderAmounts == null || orderFoods == null || databaseObject == null)
			return 0;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		String foodID = "";
		double index = 0;
		double loopSize = 0;
		double amount = 0;
		double baseExp = 0;
		double totalBaseExp = 0;
		double avarageQuantity = 0;
		database = databaseObject;
		foodArray = orderFoods;
		foodAmountArray = orderAmounts;
		loopSize = Math.min(foodArray.size(), foodAmountArray.size());
		for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
			foodID = foodArray.get((int) index).getAsString();
			amount = foodAmountArray.get((int) index).getAsDouble();
			baseExp = GetFoodBaseExpByIDProcedure.execute(database, foodID);
			avarageQuantity = GetFoodAvarageOrderQuantityByIDProcedure.execute(database, new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).getMaxStackSize(), foodID);
			totalBaseExp = totalBaseExp + (baseExp * amount) / avarageQuantity;
			index = index + 1;
		}
		return totalBaseExp;
	}
}