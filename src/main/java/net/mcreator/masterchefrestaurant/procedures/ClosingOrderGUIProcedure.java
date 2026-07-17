package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.network.ClosingOrderGUIPacketToServerMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class ClosingOrderGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		ItemStack item = ItemStack.EMPTY;
		double amountItems = 0;
		item = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(0).getItem() : ItemStack.EMPTY).copy();
		{
			MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
			_vars.OrderSlot0Item = item.copy();
			_vars.markSyncDirty();
		}
		if (world.isClientSide())
			PacketDistributor.sendToServer(new ClosingOrderGUIPacketToServerMessage(("" + item)));
	}
}