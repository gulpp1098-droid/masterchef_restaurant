package net.mcreator.omnichef.procedures;

public class RemoveFoodFromArrayProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray array, String foodID) {
		if (array == null || foodID == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonArray result = new com.google.gson.JsonArray();
		double index = 0;
		result = array;
		for (int _i1 = 0; _i1 < (int) result.size(); _i1++) {
			if ((foodID).equals(result.get((int) index).getAsString())) {
				result.remove(index);
				break;
			}
			index = index + 1;
		}
		return result;
	}
}