package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class MenuViewTitleReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).MenuShowNext) {
			return "Next Menu";
		}
		return "Current Menu";
	}
}