package net.mcreator.masterchefrestaurant.item.inventory;

import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;

import net.mcreator.masterchefrestaurant.world.inventory.ChefsDiaryGUIMenu;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;

import javax.annotation.Nonnull;

@EventBusSubscriber
public class ChefsDiaryInventoryCapability extends ComponentItemHandler {
	@SubscribeEvent
	public static void onItemDropped(ItemTossEvent event) {
		if (event.getEntity().getItem().getItem() == MasterchefRestaurantModItems.CHEFS_DIARY.get()) {
			Player player = event.getPlayer();
			if (player.containerMenu instanceof ChefsDiaryGUIMenu)
				player.closeContainer();
		}
	}

	public ChefsDiaryInventoryCapability(MutableDataComponentHolder parent) {
		super(parent, DataComponents.CONTAINER, 9);
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() != MasterchefRestaurantModItems.CHEFS_DIARY.get();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return super.getStackInSlot(slot).copy();
	}
}