package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class OrderResultSatisfiedTextProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		Entity player = null;
		player = entity;
		if (player.getData(OmnichefModVariables.PLAYER_VARIABLES).OrderResultSatisfaction >= 50 && player.getData(OmnichefModVariables.PLAYER_VARIABLES).OrderResultSatisfaction < 100) {
			return "SATISFIED";
		}
		return "";
	}
}