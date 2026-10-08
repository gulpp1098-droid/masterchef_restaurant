package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;

public class ComparePreparedDishWithOrderProcedure {
	public static com.google.gson.JsonObject execute(ItemStack preparedDish, com.google.gson.JsonArray orderAmountArray, com.google.gson.JsonArray orderFoodArray, com.google.gson.JsonObject databaseObject) {
		if (orderAmountArray == null || orderFoodArray == null || databaseObject == null)
			return new com.google.gson.JsonObject();
		ItemStack dishStack = ItemStack.EMPTY;
		com.google.gson.JsonArray orderFoodArr = new com.google.gson.JsonArray();
		com.google.gson.JsonArray orderAmountArr = new com.google.gson.JsonArray();
		com.google.gson.JsonArray dishFoodArr = new com.google.gson.JsonArray();
		com.google.gson.JsonArray dishAmountArr = new com.google.gson.JsonArray();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject resultObject = new com.google.gson.JsonObject();
		String foodID = "";
		double index = 0;
		double loopSize = 0;
		double expectedAmount = 0;
		double deliveredAmount = 0;
		double matchedAmount = 0;
		double missingForFood = 0;
		double excessForFood = 0;
		double unitScore = 0;
		double orderedAmount = 0;
		double correctAmount = 0;
		double missingAmount = 0;
		double excessAmount = 0;
		double wrongAmount = 0;
		double orderedScore = 0;
		double correctScore = 0;
		double missingScore = 0;
		double excessScore = 0;
		double wrongScore = 0;
		dishStack = preparedDish.copy();
		orderFoodArr = orderFoodArray;
		orderAmountArr = orderAmountArray;
		database = databaseObject;
		dishFoodArr = GetPreparedDishFoodArrayProcedure.execute(dishStack);
		dishAmountArr = GetPreparedDishAmountArrayProcedure.execute(dishStack);
		loopSize = Math.min(orderFoodArr.size(), orderAmountArr.size());
		for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
			foodID = orderFoodArr.get((int) index).getAsString();
			expectedAmount = GetFoodAmountFromArraysProcedure.execute(orderAmountArr, orderFoodArr, foodID);
			deliveredAmount = GetFoodAmountFromArraysProcedure.execute(dishAmountArr, dishFoodArr, foodID);
			unitScore = GetFoodScoreByIDProcedure.execute(database, foodID);
			matchedAmount = Math.min(expectedAmount, deliveredAmount);
			missingAmount = Math.max(0, expectedAmount - deliveredAmount);
			excessAmount = Math.max(0, deliveredAmount - expectedAmount);
			orderedAmount = orderedAmount + expectedAmount;
			correctAmount = correctAmount + matchedAmount;
			missingAmount = missingAmount + missingForFood;
			excessAmount = excessAmount + excessForFood;
			orderedScore = orderedScore + expectedAmount * unitScore;
			correctScore = correctScore + matchedAmount * unitScore;
			missingScore = missingScore + missingForFood * unitScore;
			excessScore = excessScore + excessForFood * unitScore;
			index = index + 1;
		}
		index = 0;
		loopSize = Math.min(dishFoodArr.size(), dishAmountArr.size());
		for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
			foodID = dishFoodArr.get((int) index).getAsString();
			expectedAmount = GetFoodAmountFromArraysProcedure.execute(orderAmountArr, orderFoodArr, foodID);
			if (expectedAmount == 0) {
				deliveredAmount = dishFoodArr.get((int) index).getAsDouble();
				unitScore = GetFoodScoreByIDProcedure.execute(database, foodID);
				wrongAmount = wrongAmount + deliveredAmount;
				wrongScore = wrongScore * unitScore;
			}
			index = index + 1;
		}
		resultObject.addProperty("ordered_amount", orderedAmount);
		resultObject.addProperty("correct_amount", correctAmount);
		resultObject.addProperty("missing_amount", missingAmount);
		resultObject.addProperty("excess_amount", excessAmount);
		resultObject.addProperty("wrong_amount", wrongAmount);
		resultObject.addProperty("ordered_score", orderedScore);
		resultObject.addProperty("correct_score", correctScore);
		resultObject.addProperty("missing_score", missingScore);
		resultObject.addProperty("excess_score", excessScore);
		resultObject.addProperty("wrong_score", wrongScore);
		resultObject.addProperty("exact", ((missingAmount == 0) == ((excessAmount == 0) == (wrongAmount == 0))));
		return resultObject;
	}
}