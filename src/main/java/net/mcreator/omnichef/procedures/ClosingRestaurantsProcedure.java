package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

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
		double openedDay = 0;
		String owner = "";
		Entity ownerEntity = null;
		if (world.dayTime() % 100 == 0) {
			index = 0;
			RestaurantsOpen.addAll(OmnichefModVariables.MapVariables.get(world).RestaurantsOpen);
			for (int _i1 = 0; _i1 < (int) RestaurantsOpen.size(); _i1++) {
				RestaurantID = RestaurantsOpen.get((int) index) instanceof Double _doub3 ? _doub3 : 0.0D;
				RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, RestaurantID);
				if (RestaurantIndex >= 0) {
					CloseTime = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"close_time");
					openedDay = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"last_day_open");
					if (Math.floor(world.dayTime() / 24000d) > openedDay || CloseTime <= world.dayTime() % 24000) {
						ModifyRestaurantLogicParameterProcedure.execute(false, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"open");
						ModifyRestaurantLogicParameterProcedure.execute(true, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"menu_advance_ready");
						OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.remove((int) OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.indexOf((RestaurantsOpen.get((int) index) instanceof Double _doub6 ? _doub6 : 0.0D)));
						MarkRestaurantsOpenDirtyProcedure.execute(world);
						if (world instanceof ServerLevel _level) {
							_level.getServer().getPlayerList()
									.broadcastSystemMessage(Component.literal(("["
											+ GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
													OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "ownerName")
											+ "] " + GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
													OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "name")
											+ " restaurant has been closed.")), false);
						}
						owner = GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"owner");
						ownerEntity = world instanceof net.minecraft.server.level.ServerLevel serverLevel ? serverLevel.getServer().getPlayerList().getPlayer(java.util.UUID.fromString(owner)) : null;
						if (ownerEntity != null) {
							if (ownerEntity instanceof Player _player12 && !_player12.level().isClientSide())
								_player12.displayClientMessage(Component.literal("Your restaurant has been closed for today.").withStyle(ChatFormatting.GOLD), false);
						}
						if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
							net.minecraft.server.level.ServerPlayer onlineOwner = null;
							try {
								onlineOwner = serverLevel.getServer().getPlayerList().getPlayer(java.util.UUID.fromString(owner));
							} catch (IllegalArgumentException ignored) {
							}
							if (onlineOwner != null) {
								CreateOverlayDataTransferProcedure.execute(world, onlineOwner);
							}
						}
					}
				}
				index = index + 1;
			}
		}
	}
}