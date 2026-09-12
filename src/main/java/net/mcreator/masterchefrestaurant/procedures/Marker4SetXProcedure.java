package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class Marker4SetXProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		double Xvalue = 0;
		Xvalue = CalculateTimelineMarkerXProcedure.execute(entity, 3);
		return Xvalue;
	}
}