package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class DebugReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		return entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Debug;
	}
}