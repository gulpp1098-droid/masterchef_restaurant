package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MenuPacketToServerMessage;
import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class FillOrderSlotsProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.isClientSide())
			PacketDistributor.sendToServer(new MenuPacketToServerMessage(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientUUID));
	}
}