package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.network.MenuPacketToClientMessage;
import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.util.UUID;

public class MenuPacketToServerProcedureProcedure {
	public static void execute(LevelAccessor world, Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		if ((world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, inboundString) : null) != null && CanUseCurrentClientOrderSessionProcedure.execute(world, entity)
				&& (inboundString).equals(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientUUID)) {
			if (entity instanceof ServerPlayer player3)
				PacketDistributor.sendToPlayer(player3, new MenuPacketToClientMessage(((world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, inboundString) : null).getPersistentData().getString("food"))));
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}