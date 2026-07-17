package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class ClientFollowStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Entity leader = null;
		client = entity;
		if ((world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, (client.getPersistentData().getString("leaderUUID"))) : null) != null
				&& !((client.getPersistentData().getString("state")).equals("order_pick") || (client.getPersistentData().getString("state")).equals("order_wait") || (client.getPersistentData().getString("state")).equals("food_wait")
						|| (client.getPersistentData().getString("state")).equals("food_eat") || (client.getPersistentData().getString("state")).equals("group_wait"))) {
			leader = world instanceof ServerLevel _level8 ? getEntityFromUUID(_level8, (client.getPersistentData().getString("leaderUUID"))) : null;
			client.getPersistentData().putString("state", (leader.getPersistentData().getString("state")));
			client.getPersistentData().putDouble("stuckCounter", 10);
			client.getPersistentData().putDouble("DestX", (leader.getPersistentData().getDouble("DestX")));
			client.getPersistentData().putDouble("DestY", (leader.getPersistentData().getDouble("DestY")));
			client.getPersistentData().putDouble("DestZ", (leader.getPersistentData().getDouble("DestZ")));
			if (client instanceof Mob _entity)
				_entity.getNavigation().moveTo((leader.getX()), (leader.getY()), (leader.getZ()), 1);
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