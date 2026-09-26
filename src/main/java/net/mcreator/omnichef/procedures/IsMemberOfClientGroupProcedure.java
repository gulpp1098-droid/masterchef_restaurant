package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.ClientEntity;

public class IsMemberOfClientGroupProcedure {
	public static boolean execute(Entity client, Entity entity) {
		if (client == null || entity == null)
			return false;
		Entity leader = null;
		Entity candidate = null;
		leader = entity;
		candidate = client;
		if (candidate instanceof ClientEntity && leader.getPersistentData().getDouble("RestaurantID") == candidate.getPersistentData().getDouble("RestaurantID")
				&& leader.getPersistentData().getDouble("group") == candidate.getPersistentData().getDouble("group") && (leader == candidate || (candidate.getPersistentData().getString("leaderUUID")).equals(leader.getStringUUID()))) {
			return true;
		}
		return false;
	}
}