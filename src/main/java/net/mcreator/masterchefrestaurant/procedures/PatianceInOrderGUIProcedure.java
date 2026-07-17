package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.network.PatiancePacketToClientMessage;
import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.util.UUID;

public class PatianceInOrderGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ServerPlayer player2)
			PacketDistributor.sendToPlayer(player2, new PatiancePacketToClientMessage((new java.text.DecimalFormat("##")
					.format((world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientUUID) : null).getPersistentData().getDouble("patience")))));
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}