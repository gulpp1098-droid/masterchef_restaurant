package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class ClientPatianceGoingDownProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double nbtPatience = 0;
		Entity client = null;
		client = entity;
		nbtPatience = client.getPersistentData().getDouble("patience");
		if ((client.getPersistentData().getString("state")).equals("food_wait") || (client.getPersistentData().getString("state")).equals("queue_wait") || (client.getPersistentData().getString("state")).equals("order_wait")
				|| (client.getPersistentData().getString("state")).equals("queue_move")) {
			if (nbtPatience > 0) {
				client.getPersistentData().putDouble("patience", (nbtPatience - 1));
			} else {
				ClientExpPayProcedure.execute(world, entity);
				client.getPersistentData().putString("state", "group_wait");
			}
		}
	}
}