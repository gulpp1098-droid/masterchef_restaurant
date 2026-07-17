package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class OpenManagmentGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonObject JSONObject = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			CreateGUIDataTransferProcedure.execute(world, entity);
		}
	}
}