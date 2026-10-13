package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;

public class CreateMenuFoodDisplayStackProcedure {
	public static ItemStack execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return ItemStack.EMPTY;
		ItemStack displayStack = ItemStack.EMPTY;
		displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
		{
			final String _tagName = "menu_food_tier";
			final double _tagValue = GetFoodTierByIDProcedure.execute(databaseObject, foodID);
			CustomData.update(DataComponents.CUSTOM_DATA, displayStack, tag -> tag.putDouble(_tagName, _tagValue));
		}
		return displayStack;
	}
}