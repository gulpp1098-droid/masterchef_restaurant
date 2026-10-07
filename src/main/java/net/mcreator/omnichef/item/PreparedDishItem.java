package net.mcreator.omnichef.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class PreparedDishItem extends Item {
	public PreparedDishItem() {
		super(new Item.Properties().stacksTo(1));
	}

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged && !oldStack.equals(newStack);
	}
}