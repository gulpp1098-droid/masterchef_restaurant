package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class Food5NameReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		return ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(4).getItem() : ItemStack.EMPTY).getDisplayName().getString()).substring(
				(int) ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(4).getItem() : ItemStack.EMPTY).getDisplayName().getString()).indexOf("[")
						+ "[".length(),
				(int) ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(4).getItem() : ItemStack.EMPTY).getDisplayName().getString())
						.lastIndexOf("]"));
	}
}