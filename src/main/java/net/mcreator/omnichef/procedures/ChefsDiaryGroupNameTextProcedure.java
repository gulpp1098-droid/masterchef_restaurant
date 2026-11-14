package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryGroupNameTextProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodGroup).equals("ingredient")) {
			return "INGREDIENTS";
		}
		return "FOODS";
	}
}