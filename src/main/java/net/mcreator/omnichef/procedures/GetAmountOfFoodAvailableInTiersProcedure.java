package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Tier;

public class GetAmountOfFoodAvailableInTiersProcedure {
	public static double execute(LevelAccessor world, double amountOfTiersDependency, double minTierDependency) {
		com.google.gson.JsonArray Tier = new com.google.gson.JsonArray();
		double TotalAmount = 0;
		double index = 0;
		index = minTierDependency;
		for (int _i1 = 0; _i1 < (int) amountOfTiersDependency; _i1++) {
			Tier = GetFoodDataFromTierProcedure.execute(world, index);
			TotalAmount = TotalAmount + Tier.size();
			index = index + 1;
		}
		return TotalAmount;
	}
}