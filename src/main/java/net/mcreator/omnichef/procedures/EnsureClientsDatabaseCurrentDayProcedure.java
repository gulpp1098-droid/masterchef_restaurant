package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class EnsureClientsDatabaseCurrentDayProcedure {
	public static boolean execute(LevelAccessor world) {
		double currentDay = 0;
		currentDay = Math.floor(world.dayTime() / 24000d);
		if (OmnichefModVariables.MapVariables.get(world).LastClientsDatabaseResetDay < currentDay) {
			DeleteClientsDatabaseFileProcedure.execute(world);
		}
		return OmnichefModVariables.MapVariables.get(world).LastClientsDatabaseResetDay >= currentDay;
	}
}