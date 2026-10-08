package net.mcreator.omnichef.procedures;

public class GetFoodAmountFromArraysProcedure {
	public static double execute(com.google.gson.JsonArray foodAmountArray, com.google.gson.JsonArray foodArray, String foodID) {
		if (foodAmountArray == null || foodArray == null || foodID == null)
			return 0;
		double index = 0;
		double loopSize = 0;
		double totalAmount = 0;
		com.google.gson.JsonArray foodArr = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArr = new com.google.gson.JsonArray();
		String foodString = "";
		foodArr = foodArray;
		foodAmountArr = foodAmountArray;
		foodString = foodID;
		loopSize = Math.min(foodArr.size(), foodAmountArr.size());
		for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
			if ((foodArr.get((int) index).getAsString()).equals(foodString)) {
				totalAmount = totalAmount + foodAmountArr.get((int) index).getAsDouble();
			}
			index = index + 1;
		}
		return index;
	}
}