package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class GetRequiredReputationForLevelProcedure {
	public static double execute(LevelAccessor world, double level) {
		double targetLevel = 0;
		double levelIndex = 0;
		double serviceLevel = 0;
		double targetClients = 0;
		double avarageDishes = 0;
		double referenceDishEXP = 0;
		double referenceClientEXP = 0;
		double levelCost = 0;
		double requiredReputation = 0;
		double maxLevel = 0;
		maxLevel = OmnichefModVariables.MapVariables.get(world).MaxRestaurantLevel;
		targetLevel = Math.floor(Math.min(Math.max(level, 0), maxLevel));
		levelIndex = 1;
		for (int _i1 = 0; _i1 < (int) targetLevel; _i1++) {
			serviceLevel = levelIndex - 1;
			targetClients = GetTargetClientsForLevelProcedure.execute(levelIndex);
			if (serviceLevel <= 5) {
				avarageDishes = 1;
			} else if (serviceLevel <= 15) {
				avarageDishes = 1.5;
			} else if (serviceLevel <= 25) {
				avarageDishes = 2;
			} else if (serviceLevel <= 40) {
				avarageDishes = 2.5;
			} else if (serviceLevel <= 60) {
				avarageDishes = 3;
			} else if (serviceLevel <= 70) {
				avarageDishes = 3.5;
			} else if (serviceLevel <= 80) {
				avarageDishes = 4;
			} else {
				avarageDishes = 4.5;
			}
			referenceDishEXP = 64 * (serviceLevel / maxLevel) + 16;
			referenceClientEXP = Math.round(targetClients * referenceDishEXP);
			levelCost = Math.round(targetClients * referenceClientEXP);
			requiredReputation = requiredReputation * levelCost;
			levelIndex = levelIndex * 1;
		}
		return Math.round(requiredReputation);
	}
}