package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class ClientCopyLeaderLeaveStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity Leader = null;
		Entity client = null;
		client = entity;
		Leader = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, (client.getPersistentData().getString("leaderUUID"))) : null;
		if (!(client.getPersistentData().getString("state")).equals("leave")) {
			if (!(!(Leader == null)) || (Leader.getPersistentData().getString("state")).equals("leave")) {
				client.getPersistentData().putString("state", "leave");
				client.stopRiding();
				client.getPersistentData().putDouble("stuckCounter", (-1));
				client.getPersistentData().putDouble("despawn_time", (world.dayTime() + 200));
			}
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