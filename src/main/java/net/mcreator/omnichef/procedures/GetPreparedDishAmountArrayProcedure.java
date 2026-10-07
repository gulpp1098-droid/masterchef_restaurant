package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

import java.util.Arrays;
import java.util.ArrayList;

public class GetPreparedDishAmountArrayProcedure {
	public static ArrayList execute(ItemStack itemstack) {
		String foodText = "";
		ItemStack preparedDish = ItemStack.EMPTY;
		ArrayList<Object> foodArray = new ArrayList<>();
		preparedDish = itemstack.copy();
		foodText = preparedDish.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("food_amount");
		foodArray = string2ArrayList(foodText, ",");
		return foodArray;
	}

	private static ArrayList<Object> string2ArrayList(String text, String separator) {
		return new ArrayList<>(Arrays.asList(text.split(separator)));
	}
}