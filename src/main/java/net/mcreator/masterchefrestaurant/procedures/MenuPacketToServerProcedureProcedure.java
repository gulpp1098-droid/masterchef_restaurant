package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.network.MenuPacketToClientMessage;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import java.util.UUID;

public class MenuPacketToServerProcedureProcedure {
	public static void execute(LevelAccessor world, Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		MasterchefRestaurantMod.LOGGER.info(inboundString);
		MasterchefRestaurantMod.LOGGER.info(world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, inboundString) : null);
		if ((world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, inboundString) : null) != null) {
			if (entity instanceof ServerPlayer player4)
				PacketDistributor.sendToPlayer(player4, new MenuPacketToClientMessage(((world instanceof ServerLevel _level2 ? getEntityFromUUID(_level2, inboundString) : null).getPersistentData().getString("food"))));
			MasterchefRestaurantMod.LOGGER.info("Sent: " + ((world instanceof ServerLevel _level5 ? getEntityFromUUID(_level5, inboundString) : null).getPersistentData().getString("food")));
		} else {
			MasterchefRestaurantMod.LOGGER.info("Missing: " + ((world instanceof ServerLevel _level7 ? getEntityFromUUID(_level7, inboundString) : null).getPersistentData().getString("food")));
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