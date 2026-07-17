package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.s;
import org.checkerframework.checker.units.qual.Area;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.entity.LocationAreaEntity;

public class LocationAreaColorChangeProcedure {
	public static void execute(LevelAccessor world, double x, double z, Entity entity) {
		if (entity == null)
			return;
		Entity Area = null;
		com.google.gson.JsonArray localizationArray = new com.google.gson.JsonArray();
		double PosX = 0;
		double PosZ = 0;
		double RestaurantID = 0;
		double localizationIndex = 0;
		double SectionX = 0;
		double SectionZ = 0;
		boolean ReturnYellow = false;
		boolean ReturnGreen = false;
		String localizationString = "";
		if (!world.isClientSide()) {
			Area = entity;
			ReturnGreen = false;
			ReturnYellow = false;
			PosX = Math.floor(x / 5);
			PosZ = Math.floor(z / 5);
			RestaurantID = Area.getPersistentData().getDouble("RestaurantID");
			localizationArray = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, RestaurantID), "restaurants", "locations", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			if (localizationArray.isEmpty()) {
				if (Area instanceof LocationAreaEntity _datEntSetI)
					_datEntSetI.getEntityData().set(LocationAreaEntity.DATA_AreaState, 1);
			} else {
				for (int index0 = 0; index0 < (int) localizationArray.size(); index0++) {
					localizationString = localizationArray.get((int) localizationIndex).getAsString();
					SectionX = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(0, localizationString));
					SectionZ = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(1, localizationString));
					localizationIndex = localizationIndex + 1;
					if (SectionX == PosX && SectionZ == PosZ) {
						ReturnGreen = true;
						break;
					} else if (Math.abs(PosX - SectionX) + Math.abs(PosZ - SectionZ) == 1) {
						ReturnYellow = true;
					}
				}
				if (ReturnGreen) {
					if (Area instanceof LocationAreaEntity _datEntSetI)
						_datEntSetI.getEntityData().set(LocationAreaEntity.DATA_AreaState, 0);
				} else if (ReturnYellow) {
					if (Area instanceof LocationAreaEntity _datEntSetI)
						_datEntSetI.getEntityData().set(LocationAreaEntity.DATA_AreaState, 1);
				} else {
					if (Area instanceof LocationAreaEntity _datEntSetI)
						_datEntSetI.getEntityData().set(LocationAreaEntity.DATA_AreaState, 2);
				}
			}
			localizationIndex = 0;
		}
	}
}