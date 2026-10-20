package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public class GetTradeBoardIngredientPoolProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray unlockedArray, com.google.gson.JsonObject databaseObject) {
		if (unlockedArray == null || databaseObject == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonArray pool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedArr = new com.google.gson.JsonArray();
		com.google.gson.JsonObject foodData = new com.google.gson.JsonObject();
		String foodID = "";
		ItemStack item = ItemStack.EMPTY;
		double index = 0;
		double poolIndex = 0;
		boolean alreadyAdded = false;
		unlockedArr = unlockedArray;
		for (int _i1 = 0; _i1 < (int) unlockedArr.size(); _i1++) {
			foodID = pool.get((int) index).getAsString();
			foodData = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
			if (foodData.has("role")) {
				if ((foodData.get("role").getAsString()).equals("INGREDIENT")) {
					item = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					if (!(item.getItem() == Blocks.AIR.asItem())) {
						alreadyAdded = false;
						poolIndex = 0;
						for (int _i2 = 0; _i2 < (int) pool.size(); _i2++) {
							if ((pool.get((int) poolIndex).getAsString()).equals(foodID)) {
								alreadyAdded = true;
							}
							poolIndex = poolIndex + 1;
						}
						if (!alreadyAdded) {
							pool.add(foodID);
						}
					}
				}
			}
			index = index + 1;
		}
		return pool;
	}
}