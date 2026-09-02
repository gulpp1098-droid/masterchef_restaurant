package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

public class TimeReturnProcedure {
	public static String execute(LevelAccessor world) {
		return "Time: " + new java.text.DecimalFormat("00").format((int) ((Math.floor((world.dayTime() % 24000) / 1000d) + 6) % 24)) + ":" + new java.text.DecimalFormat("00").format((int) (Math.floor(world.dayTime() % 1000) * (60d / 1000)))
				+ " / Day: " + (int) (Math.floor(world.dayTime() / 24000d) + 1);
	}
}