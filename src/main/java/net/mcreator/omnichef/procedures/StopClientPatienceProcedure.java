package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

public class StopClientPatienceProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.getPersistentData().putBoolean("patience_needed", false);
	}
}