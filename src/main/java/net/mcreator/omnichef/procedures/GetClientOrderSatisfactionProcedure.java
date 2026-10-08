package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

public class GetClientOrderSatisfactionProcedure {
	public static double execute(Entity clientEntity) {
		if (clientEntity == null)
			return 0;
		Entity client = null;
		com.google.gson.JsonObject resultObject = new com.google.gson.JsonObject();
		double orderedScore = 0;
		double correctScore = 0;
		double excessScore = 0;
		double wrongScore = 0;
		double evaluatedScore = 0;
		double satisfaction = 0;
		client = clientEntity;
		if (!client.getPersistentData().getBoolean("order_served")) {
			return 0;
		}
		resultObject = GetClientOrderResultProcedure.execute(client);
		orderedScore = resultObject.get("ordered_score").getAsDouble();
		correctScore = resultObject.get("correct_score").getAsDouble();
		excessScore = resultObject.get("excess_score").getAsDouble();
		wrongScore = resultObject.get("wrong_score").getAsDouble();
		evaluatedScore = orderedScore + excessScore + wrongScore;
		if (evaluatedScore <= 0) {
			return 0;
		} else {
			satisfaction = Math.round(Math.pow(10, 1) * (100 * (correctScore / evaluatedScore))) / Math.pow(10, 1);
		}
		return Math.min(100, Math.max(0, satisfaction));
	}
}