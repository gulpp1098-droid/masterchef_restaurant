package net.mcreator.masterchefrestaurant.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class BellItem extends Item {
	public BellItem() {
		super(new Item.Properties());
	}

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged && !oldStack.equals(newStack);
	}
}