package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.ServePacketToServerMessage;
import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class ClientIsServedProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.isClientSide())
			PacketDistributor
					.sendToServer(new ServePacketToServerMessage(((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu0 ? _menu0.getSlots().get(0).getItem() : ItemStack.EMPTY)
							+ ":" + entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientUUID)));
	}
}