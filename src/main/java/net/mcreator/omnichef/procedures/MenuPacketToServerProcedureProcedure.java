package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.network.MenuPacketToClientMessage;

import java.util.UUID;

public class MenuPacketToServerProcedureProcedure {
	public static void execute(LevelAccessor world, Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		Entity client = null;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject menuObject = new com.google.gson.JsonObject();
		if ((world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, inboundString) : null) != null && CanUseCurrentClientOrderSessionProcedure.execute(world, entity)
				&& (inboundString).equals(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID)) {
			client = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, inboundString) : null;
			foodArray = GetClientOrderFoodArrayProcedure.execute(client);
			foodAmountArray = GetClientOrderAmountArrayProcedure.execute(client);
			menuObject.addProperty("food", foodArray);
			menuObject.addProperty("food_amount", foodAmountArray);
			if (entity instanceof ServerPlayer player4)
				PacketDistributor.sendToPlayer(player4, new MenuPacketToClientMessage(("" + menuObject)));
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