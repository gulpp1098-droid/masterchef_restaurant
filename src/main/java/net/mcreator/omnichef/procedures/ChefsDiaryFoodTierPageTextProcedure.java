package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryFoodTierPageTextProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		return "Food Tier: " + (int) entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodTier + " | Page: " + (int) entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage;
	}
}