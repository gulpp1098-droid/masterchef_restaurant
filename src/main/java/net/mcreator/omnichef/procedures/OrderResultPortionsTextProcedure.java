package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class OrderResultPortionsTextProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		Entity player = null;
		player = entity;
		return new java.text.DecimalFormat("0.#").format(player.getData(OmnichefModVariables.PLAYER_VARIABLES).OrderResultPortions) + "%";
	}
}