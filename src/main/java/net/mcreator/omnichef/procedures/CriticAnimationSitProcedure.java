package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.CriticEntity;

public class CriticAnimationSitProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof CriticEntity _datEntL0 && _datEntL0.getEntityData().get(CriticEntity.DATA_Sit);
	}
}