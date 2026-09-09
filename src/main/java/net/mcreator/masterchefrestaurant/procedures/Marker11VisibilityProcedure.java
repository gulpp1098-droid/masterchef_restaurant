package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class Marker11VisibilityProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		double Xvalue = 0;
		Xvalue = CalculateTimelineMarkerXProcedure.execute(entity, 10);
		if (Xvalue < 0) {
			return false;
		}
		return true;
	}
}