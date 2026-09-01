package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class Food6RewardReturnProcedure {
	public static String execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return "";
		double tier = 0;
		tier = GetFoodTierByItemProcedure.execute(world, entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(5).getItem() : ItemStack.EMPTY);
		if (tier == -1) {
			return "-";
		}
		return "" + (int) (tier * 2 + 2);
	}
}