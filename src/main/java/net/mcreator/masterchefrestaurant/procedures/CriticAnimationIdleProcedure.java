package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.entity.CriticEntity;

public class CriticAnimationIdleProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof CriticEntity _datEntL0 && _datEntL0.getEntityData().get(CriticEntity.DATA_Idle);
	}
}