package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.ClientEntity;

public class ClientAnimationWalkProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof ClientEntity _datEntL0 && _datEntL0.getEntityData().get(ClientEntity.DATA_Walk);
	}
}