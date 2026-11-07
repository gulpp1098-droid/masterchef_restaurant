package net.mcreator.omnichef.procedures;

public class FinishUnspawnedClientGroupProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject groupData) {
		if (groupData == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonObject member = new com.google.gson.JsonObject();
		com.google.gson.JsonArray members = new com.google.gson.JsonArray();
		double memberIndex = 0;
		group = groupData;
		if (group.get("spawned").getAsBoolean()) {
			return group;
		}
		if (group.has("finished")) {
			if (group.get("finished").getAsBoolean()) {
				return group;
			}
		}
		members = group.get("members").getAsJsonArray();
		for (int _i1 = 0; _i1 < (int) members.size(); _i1++) {
			member = members.get((int) memberIndex).getAsJsonObject();
			member.addProperty("order_served", false);
			member.addProperty("final_satisfaction", 0);
			member.addProperty("result_recorded", true);
			member.addProperty("has_missing_food", false);
			member.addProperty("has_extra_food", false);
			member.addProperty("has_wrong_food", false);
			memberIndex = memberIndex + 1;
		}
		group.addProperty("finished", true);
		return group;
	}
}