package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.LevelResource;

public class GetMasterchefWorldPathProcedure {
	public static String execute(LevelAccessor world) {
		if (world instanceof ServerLevel serverLevel) {
			return serverLevel.getServer()
					.getWorldPath(LevelResource.ROOT)
					.resolve("masterchef")
					.toString();
		}
		return "";
	}
}