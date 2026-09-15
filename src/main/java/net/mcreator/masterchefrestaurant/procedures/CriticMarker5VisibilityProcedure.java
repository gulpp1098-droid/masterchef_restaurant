package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class CriticMarker5VisibilityProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		double Xvalue = 0;
		if (IsMarkerCriticProcedure.execute(entity, 4)) {
			return "markercritic_icon.png";
		}
		return "marker_icon.png";
	}
}