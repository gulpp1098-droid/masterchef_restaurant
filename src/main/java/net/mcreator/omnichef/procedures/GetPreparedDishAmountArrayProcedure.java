package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

import net.mcreator.omnichef.OmnichefMod;

public class GetPreparedDishAmountArrayProcedure {
	public static com.google.gson.JsonArray execute(ItemStack itemstack) {
		String foodText = "";
		ItemStack preparedDish = ItemStack.EMPTY;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject wrapperObject = new com.google.gson.JsonObject();
		preparedDish = itemstack.copy();
		foodText = preparedDish.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("food_amount");
		if ((foodText).equals("")) {
			foodText = "[]";
		}
		wrapperObject = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(("{\"data\":" + "" + foodText + "}"));
		foodArray = wrapperObject.get("data").getAsJsonArray();
		return foodArray;
	}
}