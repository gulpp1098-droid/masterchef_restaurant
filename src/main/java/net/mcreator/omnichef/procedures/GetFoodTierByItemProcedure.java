package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;

import java.io.File;

public class GetFoodTierByItemProcedure {
	public static double execute(ItemStack itemDependency) {
		File foodDatabaseFile = new File("");
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		String foodID = "";
		boolean found = false;
		double tier = 0;
		double foodIndex = 0;
		double tierIndex = 0;
		tier = -1;
		foodID = BuiltInRegistries.ITEM.getKey(itemDependency.getItem()).toString();
		if ((foodID).equals("minecraft:air")) {
			return tier;
		}
		return itemDependency.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("menu_food_tier");
	}
}