package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ShouldShowOrderResultOverlayProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity player = null;
		player = entity;
		return player.getData(OmnichefModVariables.PLAYER_VARIABLES).OrderResultHideTime > world.dayTime();
	}
}