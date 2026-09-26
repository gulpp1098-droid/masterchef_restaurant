package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class PatianceReturnProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getData(OmnichefModVariables.PLAYER_VARIABLES).ClientPatiance;
	}
}