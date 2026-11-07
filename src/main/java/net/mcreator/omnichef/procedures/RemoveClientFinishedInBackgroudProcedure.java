package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class RemoveClientFinishedInBackgroudProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject clientData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		double groupIndex = 0;
		if (world.isClientSide()) {
			return false;
		}
		if (entity.getPersistentData().getDouble("client_expire_time") <= 0) {
			return false;
		}
		if (world.dayTime() < entity.getPersistentData().getDouble("client_expire_time")) {
			return false;
		}
		clientData = FindClientsInfoByIndexViaIDProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID"));
		if (!clientData.has("shift_id")) {
			return false;
		}
		if (!(clientData.get("shift_id").getAsDouble() == entity.getPersistentData().getDouble("shift_id"))) {
			return false;
		}
		groups = clientData.get("groups").getAsJsonArray();
		groupIndex = entity.getPersistentData().getDouble("group");
		if (groupIndex < 0 || groupIndex >= groups.size()) {
			return false;
		}
		group = groups.get((int) groupIndex).getAsJsonObject();
		if (group.has("finished")) {
			if (group.get("finished").getAsBoolean()) {
				entity.getPersistentData().putBoolean("active_group_finished", true);
				entity.stopRiding();
				if (!entity.level().isClientSide())
					entity.discard();
				return true;
			}
		}
		return false;
	}
}