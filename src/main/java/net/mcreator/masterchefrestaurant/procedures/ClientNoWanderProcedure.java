package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class ClientNoWanderProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		Entity client = null;
		client = entity;
		if ((client.getPersistentData().getString("state")).equals("queue_move") || (client.getPersistentData().getString("state")).equals("queue_wait") || (client.getPersistentData().getString("state")).equals("table_go")
				|| (client.getPersistentData().getString("state")).equals("order_pick") || (client.getPersistentData().getString("state")).equals("order_wait") || (client.getPersistentData().getString("state")).equals("food_wait")
				|| (client.getPersistentData().getString("state")).equals("food_eat") || (client.getPersistentData().getString("state")).equals("pay") || (client.getPersistentData().getString("state")).equals("find_chair")
				|| (client.getPersistentData().getString("state")).equals("group_wait") || (client.getPersistentData().getString("state")).equals("leave")) {
			return false;
		}
		return true;
	}
}