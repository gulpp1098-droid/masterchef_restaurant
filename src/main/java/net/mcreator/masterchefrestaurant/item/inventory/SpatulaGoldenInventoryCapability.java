package net.mcreator.masterchefrestaurant.item.inventory;

import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;

import net.mcreator.masterchefrestaurant.world.inventory.RestaurantManagementGUIMenu;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;

import javax.annotation.Nonnull;

@EventBusSubscriber
public class SpatulaGoldenInventoryCapability extends ComponentItemHandler {
	@SubscribeEvent
	public static void onItemDropped(ItemTossEvent event) {
		if (event.getEntity().getItem().getItem() == MasterchefRestaurantModItems.SPATULA_GOLDEN.get()) {
			Player player = event.getPlayer();
			if (player.containerMenu instanceof RestaurantManagementGUIMenu)
				player.closeContainer();
		}
	}

	public SpatulaGoldenInventoryCapability(MutableDataComponentHolder parent) {
		super(parent, DataComponents.CONTAINER, 3);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 98;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() != MasterchefRestaurantModItems.SPATULA_GOLDEN.get();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return super.getStackInSlot(slot).copy();
	}
}