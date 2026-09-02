package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.util.ArrayList;

import java.io.File;

public class OpenRestaurantButtonProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity owner = null;
		File RestaurantFile = new File("");
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantChange = new com.google.gson.JsonObject();
		com.google.gson.JsonArray Restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray locationsArray = new com.google.gson.JsonArray();
		ArrayList<Object> openedRestaurants = new ArrayList<>();
		double index = 0;
		double AmountOfTables = 0;
		double AmountOfChairs = 0;
		double ChairsMax = 0;
		double arrayIndex = 0;
		if (!world.isClientSide()) {
			owner = entity;
			index = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
			if (index >= 0) {
				locationsArray = GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "locations", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
				if (!locationsArray.isEmpty()) {
					Restaurant = FindRestaurantInfoByIndexViaIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
					if (Restaurant.get("open").getAsBoolean()) {
						openedRestaurants = MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen;
						arrayIndex = 0;
						for (int _i1 = 0; _i1 < (int) MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen.size(); _i1++) {
							if ((openedRestaurants.get((int) arrayIndex) instanceof Double _doub4 ? _doub4 : 0.0D) == entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID) {
								MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen.remove((int) arrayIndex);
								break;
							}
							arrayIndex = arrayIndex + 1;
						}
						ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open");
						CreateGUIDataTransferProcedure.execute(world, entity);
						FillSlotsMenuProcedure.execute(world, entity);
						if (owner instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("Restaurant is now closed!"), false);
					} else if (!Restaurant.get("open").getAsBoolean()) {
						if (GetRestaurantNumberParameterProcedure.execute(index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "last_day_open") < Math.floor(world.dayTime() / 24000d)) {
							if (world.dayTime() % 24000 >= 50 % 24000 && world.dayTime() % 24000 <= 9000 % 24000) {
								AmountOfTables = ScanningRestaurantAreasProcedure.execute(world, entity, "Tables");
								AmountOfChairs = ScanningRestaurantAreasProcedure.execute(world, entity, "Chairs");
								ChairsMax = ScanningRestaurantAreasProcedure.execute(world, entity, "ChairsMax");
								if (AmountOfTables > 0) {
									if (AmountOfChairs > 0) {
										if (!(GetRestaurantStringParameterProcedure.execute(index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
												MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception")).equals("")) {
											ModifyRestaurantWholeArrayParameterProcedure.execute(
													GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "next_menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
															MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path),
													index, "restaurants", "menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
											GenerateRestaurantMenuProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID, GetRestaurantNumberParameterProcedure.execute(index, "restaurants",
													MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level"));
											ModifyRestaurantNumberParameterProcedure.execute(Math.floor(world.dayTime() / 24000d), index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
													MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "last_day_open");
											ModifyRestaurantLogicParameterProcedure.execute(true, index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
													MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open");
											ModifyRestaurantNumberParameterProcedure.execute(world.dayTime() % 24000 + 8000, index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
													MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "close_time");
											MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen.add(owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
											MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
											RestaurantIsOpenProcedure.execute(world, entity, ChairsMax, AmountOfTables);
											CreateGUIDataTransferProcedure.execute(world, entity);
											if (owner instanceof Player _player && !_player.level().isClientSide())
												_player.displayClientMessage(Component.literal(("Your tables available: " + new java.text.DecimalFormat("#").format(AmountOfTables))), false);
											if (owner instanceof Player _player && !_player.level().isClientSide())
												_player.displayClientMessage(Component.literal(("Your chairs available: " + new java.text.DecimalFormat("#").format(AmountOfChairs))), false);
											if (owner instanceof Player _player && !_player.level().isClientSide())
												_player.displayClientMessage(Component.literal("Restaurant is now open!"), false);
										} else {
											if (owner instanceof Player _player && !_player.level().isClientSide())
												_player.displayClientMessage(Component.literal("You do NOT have reception in your restaurant area!"), false);
										}
									} else {
										if (owner instanceof Player _player && !_player.level().isClientSide())
											_player.displayClientMessage(Component.literal("You do NOT have any chairs in your restaurant area!"), false);
									}
								} else {
									if (owner instanceof Player _player && !_player.level().isClientSide())
										_player.displayClientMessage(Component.literal("You do NOT have any tables in your restaurant area!"), false);
								}
							} else {
								if (owner instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("You can open restaurant only between 6.00 AM and 12.00 PM"), false);
							}
						} else {
							if (owner instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("You cannot open restaurant twice same day!"), false);
						}
					}
				} else {
					if (owner instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("You do NOT have selected restaurant area yet!"), false);
				}
			}
		}
	}
}