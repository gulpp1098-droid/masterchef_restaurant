package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class Marker2VisibilityProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		double Xvalue = 0;
		Xvalue = CalculateTimelineMarkerXProcedure.execute(entity, 1);
		if (Xvalue < 0) {
			return false;
		}
		return true;
	}
}