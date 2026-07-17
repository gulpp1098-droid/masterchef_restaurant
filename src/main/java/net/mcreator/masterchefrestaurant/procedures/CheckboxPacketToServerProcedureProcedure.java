package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.network.CheckboxPacketToClientMessage;

import java.util.UUID;

public class CheckboxPacketToServerProcedureProcedure {
	public static void execute(LevelAccessor world, Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		if ((world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, inboundString) : null) != null) {
			if (entity instanceof ServerPlayer player3)
				PacketDistributor.sendToPlayer(player3, new CheckboxPacketToClientMessage(((world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, inboundString) : null).getPersistentData().getString("food_delivered"))));
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