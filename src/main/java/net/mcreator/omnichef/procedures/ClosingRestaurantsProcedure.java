package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.util.ArrayList;

public class ClosingRestaurantsProcedure {
	public static void execute(LevelAccessor world) {
		com.google.gson.JsonArray Restaurants = new com.google.gson.JsonArray();
		ArrayList<Object> RestaurantsOpen = new ArrayList<>();
		double index = 0;
		double CloseTime = 0;
		double RestaurantIndex = 0;
		double RestaurantID = 0;
		if (world.dayTime() % 24000 >= 7900 && world.dayTime() % 24000 <= 18000) {
			if (world.dayTime() % 100 == 0) {
				index = 0;
				RestaurantsOpen.addAll(OmnichefModVariables.MapVariables.get(world).RestaurantsOpen);
				for (int _i1 = 0; _i1 < (int) RestaurantsOpen.size(); _i1++) {
					RestaurantID = RestaurantsOpen.get((int) index) instanceof Double _doub5 ? _doub5 : 0.0D;
					RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, RestaurantID);
					CloseTime = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"close_time");
					if (CloseTime <= world.dayTime() % 24000) {
						ModifyRestaurantLogicParameterProcedure.execute(false, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"open");
						ModifyRestaurantLogicParameterProcedure.execute(true, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"menu_advance_ready");
						OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.remove((int) OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.indexOf((RestaurantsOpen.get((int) index) instanceof Double _doub7 ? _doub7 : 0.0D)));
						if (world instanceof ServerLevel _level) {
							_level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("restaurant has been closed: " + GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants",
									OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "name"))), false);
						}
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (RestaurantID == entityiterator.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID) {
								CreateOverlayDataTransferProcedure.execute(world, entityiterator);
							}
						}
					}
					index = index + 1;
				}
			}
		}
	}
}