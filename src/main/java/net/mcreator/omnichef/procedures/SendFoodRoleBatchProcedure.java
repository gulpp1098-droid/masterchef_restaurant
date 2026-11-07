package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

import net.mcreator.omnichef.network.FoodRoleSyncMessage;

public class SendFoodRoleBatchProcedure {
	public static void execute(LevelAccessor world, Entity entity, com.google.gson.JsonObject roles, boolean reset) {
		if (entity == null || roles == null)
			return;
		com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
		com.google.gson.JsonObject rolesObject = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			payload.addProperty("reset", reset);
			rolesObject = roles;
			payload.add("roles", rolesObject);
			if (entity instanceof ServerPlayer player4)
				PacketDistributor.sendToPlayer(player4, new FoodRoleSyncMessage(payload.toString()));
		}
	}
}