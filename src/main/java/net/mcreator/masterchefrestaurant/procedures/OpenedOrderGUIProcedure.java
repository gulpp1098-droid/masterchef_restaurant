package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class OpenedOrderGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.getLevelData().getGameTime() % 5 == 0) {
			if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu1 ? _menu1.getSlots().get(1).getItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
				FillOrderSlotsProcedure.execute(world, entity);
			}
			if (!((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu3 ? _menu3.getSlots().get(0).getItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem())) {
				ClientIsServedProcedure.execute(world, entity);
			}
			OrdersCheckboxesCheckProcedure.execute(world, entity);
		}
		PatianceInOrderGUIProcedure.execute(world, entity);
	}
}