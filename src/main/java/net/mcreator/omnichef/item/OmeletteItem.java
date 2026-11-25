package net.mcreator.omnichef.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class OmeletteItem extends Item {
	public OmeletteItem() {
		super(new Item.Properties().food((new FoodProperties.Builder()).nutrition(7).saturationModifier(0.6f).build()));
	}

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged && !oldStack.equals(newStack);
	}
}