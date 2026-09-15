package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class Marker18SetXProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		double Xvalue = 0;
		Xvalue = CalculateTimelineMarkerXProcedure.execute(entity, 17);
		return Xvalue;
	}
}