package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class OrdersCheckboxesCheckSlot1Procedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(1).getItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
			FillOrderSlotsProcedure.execute(world, entity);
		}
		if (!((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu2 ? _menu2.getSlots().get(0).getItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem())) {
			ClientIsServedProcedure.execute(world, entity);
		}
		OrdersCheckboxesCheckProcedure.execute(world, entity);
		if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(0, 1)).equals("0")
				|| !(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(0, 1)).equals("1")) {
			return false;
		}
		return true;
	}
}