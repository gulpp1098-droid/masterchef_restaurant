package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ShowOrderResultOverlayProcedure {
	public static void execute(LevelAccessor world, Entity clientEntity, Entity playerEntity) {
		if (clientEntity == null || playerEntity == null)
			return;
		Entity player = null;
		Entity client = null;
		com.google.gson.JsonObject resultObject = new com.google.gson.JsonObject();
		double orderedScore = 0;
		double correctScore = 0;
		double excessScore = 0;
		double wrongScore = 0;
		double accuracyTotal = 0;
		double portionTotal = 0;
		double completeness = 0;
		double accuracy = 0;
		double portions = 0;
		double satisfaction = 0;
		player = playerEntity;
		client = clientEntity;
		resultObject = GetClientOrderResultProcedure.execute(client);
		orderedScore = resultObject.get("ordered_score").getAsDouble();
		correctScore = resultObject.get("correct_score").getAsDouble();
		excessScore = resultObject.get("excess_score").getAsDouble();
		wrongScore = resultObject.get("wrong_score").getAsDouble();
		if (orderedScore > 0) {
			completeness = Math.round(Math.pow(10, 1) * ((correctScore * 100) / orderedScore)) / Math.pow(10, 1);
		} else {
			completeness = 0;
		}
		accuracyTotal = correctScore + excessScore + wrongScore;
		if (accuracyTotal > 0) {
			accuracy = Math.round(Math.pow(10, 1) * (((correctScore + excessScore) * 100) / accuracyTotal)) / Math.pow(10, 1);
		} else {
			accuracy = 0;
		}
		portionTotal = correctScore + excessScore;
		if (portionTotal > 0) {
			portions = Math.round(Math.pow(10, 1) * ((correctScore * 100) / portionTotal)) / Math.pow(10, 1);
		} else {
			portions = 0;
		}
		satisfaction = GetClientOrderSatisfactionProcedure.execute(clientEntity);
		{
			OmnichefModVariables.PlayerVariables _vars = playerEntity.getData(OmnichefModVariables.PLAYER_VARIABLES);
			_vars.OrderResultCompleteness = completeness;
			_vars.OrderResultAccuracy = accuracy;
			_vars.OrderResultPortions = portions;
			_vars.OrderResultSatisfaction = satisfaction;
			_vars.OrderResultHideTime = world.dayTime() + 60;
			_vars.markSyncDirty();
		}
	}
}