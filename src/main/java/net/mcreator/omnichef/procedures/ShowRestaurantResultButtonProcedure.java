package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ShowRestaurantResultButtonProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID > 0;
	}
}