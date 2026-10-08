package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;

import net.mcreator.omnichef.init.OmnichefModItems;

public class IsPreparedDishDataValidProcedure {
	public static boolean execute(ItemStack itemstack) {
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		double index = 0;
		boolean valid = false;
		ItemStack preparedDish = ItemStack.EMPTY;
		preparedDish = itemstack.copy();
		valid = preparedDish.getItem() == OmnichefModItems.PREPARED_DISH.get();
		if (valid) {
			foodArray = GetPreparedDishFoodArrayProcedure.execute(preparedDish);
			foodAmountArray = GetPreparedDishAmountArrayProcedure.execute(preparedDish);
			if (foodArray.isEmpty()) {
				return false;
			}
			if (foodArray.size() != foodAmountArray.size()) {
				return false;
			}
			for (int _i1 = 0; _i1 < (int) foodArray.size(); _i1++) {
				if ((foodArray.get((int) index).getAsString()).equals("")) {
					return false;
				}
				if (foodAmountArray.get((int) index).getAsDouble() <= 0) {
					return false;
				}
				index = index + 1;
			}
		}
		return valid;
	}
}