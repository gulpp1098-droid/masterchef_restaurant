package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

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
		client = entity;
		RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
		RestaurantLevel = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		CheckpointLevel = Math.floor(RestaurantLevel / 10) * 10;
		CheckpointReputation = CheckpointLevel * 40 + Math.pow(CheckpointLevel, 2) * 6 + Math.pow(CheckpointLevel, 3) * 0.08;
		foodDelivered = client.getPersistentData().getString("food_delivered");
		orderedFood = client.getPersistentData().getString("food_tiers");
		array = new Object() {
			public ArrayList<Object> convert(String text, String separator) {
				return new ArrayList<>(Arrays.asList(text.split(separator)));
			}
		}.convert(orderedFood, ",");
		String[] _array6 = foodDelivered.split(Pattern.quote(","));
		if (_array6.length != 0) {
			for (String stringiterator : _array6) {
				if ((stringiterator).equals("0")) {
					ordered = ordered + 1;
				}
				if ((stringiterator).equals("1")) {
					delivered = delivered + 1;
					ordered = ordered + 1;
				}
			}
		} else {
			String stringiterator = foodDelivered;
			for (int _yourmother = 0; _yourmother < 1; _yourmother++) {
				if ((stringiterator).equals("0")) {
					ordered = ordered + 1;
				}
				if ((stringiterator).equals("1")) {
					delivered = delivered + 1;
					ordered = ordered + 1;
				}
			}
		}
		totalMultiplayer = (100 - (ordered - delivered) * 40) / 100;
		index = 0;
		String[] _array11 = foodDelivered.split(Pattern.quote(","));
		if (_array11.length != 0) {
			for (String stringiterator : _array11) {
				if (totalMultiplayer > 0) {
					if ((stringiterator).equals("1")) {
						EXPsum = (new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(array.get((int) index) instanceof String _str8 ? _str8 : "") + 1) * 10 + EXPsum;
					}
				} else {
					if ((stringiterator).equals("0")) {
						EXPsum = (new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(array.get((int) index) instanceof String _str10 ? _str10 : "") + 1) * 10 + EXPsum;
					}
				}
				index = index + 1;
			}
		} else {
			String stringiterator = foodDelivered;
			for (int _yourmother = 0; _yourmother < 1; _yourmother++) {
				if (totalMultiplayer > 0) {
					if ((stringiterator).equals("1")) {
						EXPsum = (new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(array.get((int) index) instanceof String _str8 ? _str8 : "") + 1) * 10 + EXPsum;
					}
				} else {
					if ((stringiterator).equals("0")) {
						EXPsum = (new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(array.get((int) index) instanceof String _str10 ? _str10 : "") + 1) * 10 + EXPsum;
					}
				}
				index = index + 1;
			}
		}
		EXPTotal = EXPsum * totalMultiplayer;
		CurrentReputation = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
		if (EXPTotal > 0) {
			ModifyRestaurantNumberParameterProcedure.execute(CurrentReputation + EXPTotal, RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
		} else {
			ModifyRestaurantNumberParameterProcedure.execute(Math.max(CurrentReputation + EXPTotal, CheckpointReputation), RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
		}
		RestaurantLevelUpCheckProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
	}
}