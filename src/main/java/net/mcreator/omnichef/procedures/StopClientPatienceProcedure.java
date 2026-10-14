package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.entity.ClientEntity;

public class StopClientPatienceProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.getPersistentData().putBoolean("patience_needed", false);
		if (entity instanceof ClientEntity _datEntSetI)
			_datEntSetI.getEntityData().set(ClientEntity.DATA_PatienceBar, -1);
	}
}