package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.util.ArrayList;

public class ClosingRestaurantsProcedure {
	public static void execute(LevelAccessor world) {
		com.google.gson.JsonArray Restaurants = new com.google.gson.JsonArray();
		ArrayList<Object> RestaurantsOpen = new ArrayList<>();
		double index = 0;
		double CloseTime = 0;
		double RestaurantIndex = 0;
		if (world.dayTime() % 24000 >= 7900 && world.dayTime() % 24000 <= 18000) {
			if (world.dayTime() % 100 == 0) {
				index = 0;
				RestaurantsOpen.addAll(MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen);
				for (int _i1 = 0; _i1 < (int) RestaurantsOpen.size(); _i1++) {
					RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, RestaurantsOpen.get((int) index) instanceof Double _doub5 ? _doub5 : 0.0D);
					CloseTime = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
							MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "close_time");
					if (CloseTime <= world.dayTime() % 24000) {
						ModifyRestaurantLogicParameterProcedure.execute(false, RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open");
						GenerateRestaurantMenuProcedure.execute(world, RestaurantIndex, GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level"));
						MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen
								.remove((int) MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen.indexOf((RestaurantsOpen.get((int) index) instanceof Double _doub7 ? _doub7 : 0.0D)));
						if (world instanceof ServerLevel _level) {
							_level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("restaurant has been closed: " + GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants",
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "name"))), false);
						}
					}
					index = index + 1;
				}
			}
		}
	}
}