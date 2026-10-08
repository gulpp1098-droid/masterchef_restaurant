package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

public class GetClientSatisfactionLevelProcedure {
	public static String execute(Entity clientEntity) {
		if (clientEntity == null)
			return "";
		Entity client = null;
		double satisfaction = 0;
		client = clientEntity;
		satisfaction = GetClientOrderSatisfactionProcedure.execute(client);
		if (satisfaction >= 100) {
			return "perfect";
		} else if (satisfaction >= 50) {
			return "satisfied";
		}
		return "unsatisfied";
	}
}