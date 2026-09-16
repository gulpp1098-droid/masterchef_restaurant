package net.mcreator.masterchefrestaurant.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class GoldCoinItem extends Item {
	public GoldCoinItem() {
		super(new Item.Properties());
	}

	@Override
	public boolean isPiglinCurrency(ItemStack stack) {
		return true;
	}

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged && !oldStack.equals(newStack);
	}
}