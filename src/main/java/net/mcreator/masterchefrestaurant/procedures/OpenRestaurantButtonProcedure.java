package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

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
		com.google.gson.JsonArray menu = new com.google.gson.JsonArray();
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
						if (AreAllRestaurantGroupsSpawnedProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
							ModifyRestaurantLogicParameterProcedure.execute(true, index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready");
						} else {
							ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready");
						}
						CreateOverlayDataTransferProcedure.execute(world, entity);
						CreateGUIDataTransferProcedure.execute(world, entity);
						FillSlotsMenuProcedure.execute(world, entity);
						if (owner instanceof Player _player8 && !_player8.level().isClientSide())
							_player8.displayClientMessage(Component.literal("Restaurant closed.").withStyle(ChatFormatting.WHITE), true);
					} else if (!Restaurant.get("open").getAsBoolean() && (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.OVERWORLD) {
						if (EnsureClientsDatabaseCurrentDayProcedure.execute(world)) {
							if (GetRestaurantNumberParameterProcedure.execute(index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "last_day_open") < Math.floor(world.dayTime() / 24000d)) {
								if (world.dayTime() % 24000 >= 0 % 24000 && world.dayTime() % 24000 <= 9000 % 24000) {
									AmountOfTables = ScanningRestaurantAreasProcedure.execute(world, entity, "Tables");
									AmountOfChairs = ScanningRestaurantAreasProcedure.execute(world, entity, "Chairs");
									ChairsMax = ScanningRestaurantAreasProcedure.execute(world, entity, "ChairsMax");
									if (AmountOfTables > 0) {
										if (AmountOfChairs > 0) {
											if (!(GetRestaurantStringParameterProcedure.execute(index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
													MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception")).equals("")) {
												menu = GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
														MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
												if (menu.size() == 0 || GetRestaurantLogicParameterProcedure.execute(index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
														MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready")) {
													ModifyRestaurantWholeArrayParameterProcedure.execute(
															GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "next_menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
																	MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path),
															index, "restaurants", "menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
													GenerateRestaurantMenuProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID, GetRestaurantNumberParameterProcedure.execute(index, "restaurants",
															MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level"));
													ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
															MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready");
												}
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
												if (owner instanceof Player _player22 && !_player22.level().isClientSide())
													_player22.displayClientMessage(Component.literal("Restaurant opened successfully.").withStyle(ChatFormatting.GREEN), true);
												if (owner instanceof Player _player25 && !_player25.level().isClientSide())
													_player25.displayClientMessage(Component
															.literal(("Available setup: " + new java.text.DecimalFormat("#").format(AmountOfTables) + " Service Tables, " + new java.text.DecimalFormat("#").format(AmountOfChairs) + " Chairs."))
															.withStyle(ChatFormatting.WHITE), false);
											} else {
												if (owner instanceof Player _player28 && !_player28.level().isClientSide())
													_player28.displayClientMessage(Component.literal("Your restaurant needs a Reception.").withStyle(ChatFormatting.RED), true);
											}
										} else {
											if (owner instanceof Player _player31 && !_player31.level().isClientSide())
												_player31.displayClientMessage(Component.literal("Your restaurant needs at least one Chair").withStyle(ChatFormatting.RED), true);
										}
									} else {
										if (owner instanceof Player _player34 && !_player34.level().isClientSide())
											_player34.displayClientMessage(Component.literal("Your restaurant needs at least one Service Table.").withStyle(ChatFormatting.RED), true);
									}
								} else {
									if (owner instanceof Player _player37 && !_player37.level().isClientSide())
										_player37.displayClientMessage(Component.literal("Your restaurant can only be opened between 6:00 and 15:00.").withStyle(ChatFormatting.RED), true);
								}
							} else {
								if (owner instanceof Player _player40 && !_player40.level().isClientSide())
									_player40.displayClientMessage(Component.literal("You have already opened your restaurant today.").withStyle(ChatFormatting.RED), true);
							}
						}
					} else {
						if (owner instanceof Player _player43 && !_player43.level().isClientSide())
							_player43.displayClientMessage(Component.literal("Restaurants can only be opened in the Overworld.").withStyle(ChatFormatting.RED), true);
					}
				} else {
					if (owner instanceof Player _player46 && !_player46.level().isClientSide())
						_player46.displayClientMessage(Component.literal("You need to select a restaurant area first.").withStyle(ChatFormatting.RED), true);
				}
			}
		}
	}
}