package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class CloseOrderGUIProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
			_menu.getSlots().get(0).set(ItemStack.EMPTY);
			_player.containerMenu.broadcastChanges();
		}
		if (entity instanceof Player _player) {
			ItemStack _setstack = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).OrderSlot0Item.copy();
			_setstack.setCount(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).OrderSlot0Item.getCount());
			ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
		}
	}
}