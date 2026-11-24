package net.mcreator.omnichef.procedures;

public class GetDiaryDependentItemsProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonObject databaseObject, String selectedID) {
		if (databaseObject == null || selectedID == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject databaseObj = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject candidateData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
		com.google.gson.JsonObject diagnostics = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierItems = new com.google.gson.JsonArray();
		com.google.gson.JsonArray result = new com.google.gson.JsonArray();
		com.google.gson.JsonArray intermediateCandidates = new com.google.gson.JsonArray();
		com.google.gson.JsonArray dependencyIDs = new com.google.gson.JsonArray();
		String groupName = "";
		String candidateID = "";
		String rootID = "";
		String intermediateID = "";
		String sourceID = "";
		double groupIndex = 0;
		double tierIndex = 0;
		double itemIndex = 0;
		double sourceIndex = 0;
		double intermediateIndex = 0;
		double knownIndex = 0;
		double dependencyIndex = 0;
		boolean alreadyIncluded = false;
		boolean matched = false;
		databaseObj = databaseObject;
		technical = databaseObj.get("technical").getAsJsonObject();
		diagnostics = technical.get("diagnostics").getAsJsonObject();
		intermediateCandidates = diagnostics.get("preparation_intermediates").getAsJsonArray();
		rootID = GetFoodDiscoveryIDProcedure.execute(databaseObj, selectedID);
		dependencyIDs.add(rootID);
		while (sourceIndex < dependencyIDs.size()) {
			sourceID = dependencyIDs.get((int) sourceIndex).getAsString();
			intermediateIndex = 0;
			for (int _i1 = 0; _i1 < (int) intermediateCandidates.size(); _i1++) {
				intermediateID = intermediateCandidates.get((int) intermediateIndex).getAsString();
				alreadyIncluded = false;
				knownIndex = 0;
				for (int _i2 = 0; _i2 < (int) dependencyIDs.size(); _i2++) {
					if ((dependencyIDs.get((int) knownIndex).getAsString()).equals(intermediateID)) {
						alreadyIncluded = true;
					}
					knownIndex = knownIndex + 1;
				}
				if (!alreadyIncluded) {
					if (DoesItemDependOnProcedure.execute(databaseObj, sourceID, intermediateID)) {
						dependencyIDs.add(intermediateID);
					}
				}
				intermediateIndex = intermediateIndex + 1;
			}
			sourceIndex = sourceIndex + 1;
		}
		for (int _i1 = 0; _i1 < 2; _i1++) {
			if (groupIndex == 0) {
				groupName = "ingredients";
			} else {
				groupName = "foods";
			}
			groupObject = databaseObj.get(groupName).getAsJsonObject();
			tiersObject = groupObject.get("tiers").getAsJsonObject();
			tierIndex = 0;
			for (int _i2 = 0; _i2 < (int) groupObject.get("tier_count").getAsDouble(); _i2++) {
				tierItems = databaseObj.get((new java.text.DecimalFormat("0").format(tierIndex))).getAsJsonArray();
				itemIndex = 0;
				for (int _i3 = 0; _i3 < (int) tierItems.size(); _i3++) {
					candidateData = tierItems.get((int) itemIndex).getAsJsonObject();
					candidateID = candidateData.get("id").getAsString();
					matched = false;
					dependencyIndex = 0;
					if (!(candidateID).equals(rootID)) {
						for (int _i4 = 0; _i4 < (int) dependencyIDs.size(); _i4++) {
							if (!matched) {
								if (DoesItemDependOnProcedure.execute(databaseObj, dependencyIDs.get((int) dependencyIndex).getAsString(), candidateID)) {
									matched = true;
								}
							}
							dependencyIndex = dependencyIndex + 1;
						}
						if (matched) {
							result.add(candidateID);
						}
					}
					itemIndex = itemIndex + 1;
				}
				tierIndex = tierIndex + 1;
			}
			groupIndex = groupIndex + 1;
		}
		return result;
	}
}