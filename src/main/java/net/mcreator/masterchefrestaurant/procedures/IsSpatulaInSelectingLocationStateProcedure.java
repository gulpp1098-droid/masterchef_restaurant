package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

public class IsSpatulaInSelectingLocationStateProcedure {
	public static boolean execute(ItemStack itemstack) {
		if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SelectingRestaurantLocation")) {
			return false;
		}
		return true;
	}
}