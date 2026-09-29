package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.ArrayList;

public class ClientExpPayProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		ArrayList<Object> array = new ArrayList<>();
		Entity client = null;
		String foodDelivered = "";
		String orderedFood = "";
		double CurrentReputation = 0;
		double ordered = 0;
		double EXPsum = 0;
		double totalMultiplayer = 0;
		double index = 0;
		double delivered = 0;
		double EXPTotal = 0;
		double RestaurantIndex = 0;
		double RestaurantLevel = 0;
		double CheckpointReputation = 0;
		double CheckpointLevel = 0;
		double RealReputationChange = 0;
		double requiredReputation = 0;
		double dishEXP = 0;
		double missingpentaltyMultiplier = 0;
		double missingEXP = 0;
		double missingCount = 0;
		client = entity;
		RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
		RestaurantLevel = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		CheckpointLevel = Math.floor(RestaurantLevel / 10) * 10;
		CheckpointReputation = GetRequiredReputationForLevelProcedure.execute(world, CheckpointLevel);
		requiredReputation = GetRequiredReputationForLevelProcedure.execute(world, RestaurantLevel + 1);
		foodDelivered = client.getPersistentData().getString("food_delivered");
		orderedFood = client.getPersistentData().getString("food_exp");
		array = string2ArrayList(orderedFood, ",");
		index = 0;
		String _toSplit7 = foodDelivered;
		String[] _array7 = _toSplit7.split(Pattern.quote(","));
		for (int _iter7 = 0; _iter7 < Math.max(1, _array7.length); _iter7++) {
			String stringiterator = _array7.length == 0 ? _toSplit7 : _array7[_iter7];
			dishEXP = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(array.get((int) index) instanceof String _str4 ? _str4 : "");
			if ((stringiterator).equals("1")) {
				EXPsum = EXPsum + dishEXP;
			} else if ((stringiterator).equals("0")) {
				missingEXP = missingEXP + dishEXP;
				missingCount = missingCount + 1;
			}
			index = index + 1;
		}
		totalMultiplayer = (100 - missingCount * 40) / 100;
		if (totalMultiplayer > 0) {
			EXPTotal = Math.round(EXPsum * totalMultiplayer);
		} else {
			EXPTotal = Math.round(missingEXP * totalMultiplayer);
		}
		CurrentReputation = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"reputation");
		if (EXPTotal > 0) {
			if ((RestaurantLevel + 1) % 10 == 0) {
				ModifyRestaurantNumberParameterProcedure.execute(Math.min(CurrentReputation + EXPTotal, requiredReputation), RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
			} else {
				ModifyRestaurantNumberParameterProcedure.execute(CurrentReputation + EXPTotal, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
			}
		} else {
			if (!((RestaurantLevel + 1) % 10 == 0 && CurrentReputation >= requiredReputation)) {
				ModifyRestaurantNumberParameterProcedure.execute(Math.max(CurrentReputation + EXPTotal, CheckpointReputation), RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
			}
		}
		RealReputationChange = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"reputation") - CurrentReputation;
		ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndex, RealReputationChange, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"daily_stats", "reputation_change");
		RestaurantLevelUpCheckProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
	}

	private static ArrayList<Object> string2ArrayList(String text, String separator) {
		return new ArrayList<>(Arrays.asList(text.split(separator)));
	}
}