package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

public class DebugOverlayVisibilityProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return (entity.getDisplayName().getString()).equals("Dev");
	}
}