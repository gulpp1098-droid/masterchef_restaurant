package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;

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
		com.google.gson.JsonArray pending_cards = new com.google.gson.JsonArray();
		ArrayList<Object> openedRestaurants = new ArrayList<>();
		double index = 0;
		double AmountOfTables = 0;
		double AmountOfChairs = 0;
		double ChairsMax = 0;
		double arrayIndex = 0;
		if (!world.isClientSide()) {
			owner = entity;
			index = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
			if (index >= 0) {
				locationsArray = GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "locations", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				if (!locationsArray.isEmpty()) {
					Restaurant = FindRestaurantInfoByIndexViaIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
					if (Restaurant.get("open").getAsBoolean()) {
						openedRestaurants = OmnichefModVariables.MapVariables.get(world).RestaurantsOpen;
						arrayIndex = 0;
						for (int _i1 = 0; _i1 < (int) OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.size(); _i1++) {
							if ((openedRestaurants.get((int) arrayIndex) instanceof Double _doub4 ? _doub4 : 0.0D) == entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID) {
								openedRestaurants.remove((int) arrayIndex);
								MarkRestaurantsOpenDirtyProcedure.execute(world);
								break;
							}
							arrayIndex = arrayIndex + 1;
						}
						ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open");
						if (AreAllRestaurantGroupsSpawnedProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
							ModifyRestaurantLogicParameterProcedure.execute(true, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
									"menu_advance_ready");
						} else {
							ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
									"menu_advance_ready");
						}
						CreateOverlayDataTransferProcedure.execute(world, entity);
						CreateGUIDataTransferProcedure.execute(world, entity);
						FillSlotsMenuProcedure.execute(world, entity);
						if (owner instanceof Player _player8 && !_player8.level().isClientSide())
							_player8.displayClientMessage(Component.literal("Restaurant closed.").withStyle(ChatFormatting.WHITE), true);
					} else if (!Restaurant.get("open").getAsBoolean() && (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.OVERWORLD) {
						pending_cards = GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
						if (pending_cards.size() > 0
								|| GetRestaurantNumberParameterProcedure.execute(index, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
										"pending_unlocks") > 0
								|| GetRestaurantNumberParameterProcedure.execute(index, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
										"starter_unlocks_remaining") > 0) {
							OpenSpatulaGUIProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
						} else {
							if (EnsureClientsDatabaseCurrentDayProcedure.execute(world)) {
								if (GetRestaurantNumberParameterProcedure.execute(index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
										"last_day_open") < Math.floor(world.dayTime() / 24000d)) {
									if (world.dayTime() % 24000 >= 0 % 24000 && world.dayTime() % 24000 <= 9000 % 24000) {
										AmountOfTables = ScanningRestaurantAreasProcedure.execute(world, entity, "Tables");
										AmountOfChairs = ScanningRestaurantAreasProcedure.execute(world, entity, "Chairs");
										ChairsMax = ScanningRestaurantAreasProcedure.execute(world, entity, "ChairsMax");
										if (AmountOfTables > 0) {
											if (AmountOfChairs > 0) {
												if (IsRestaurantReceptionValidProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
													menu = GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
															OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
													if (menu.size() == 0 || GetRestaurantLogicParameterProcedure.execute(index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
															OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready")) {
														ModifyRestaurantWholeArrayParameterProcedure.execute(
																GetRestaurantArrayParameterProcedure.execute(index, "restaurants", "next_menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
																		OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path),
																index, "restaurants", "menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
														GenerateRestaurantMenuProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID, GetRestaurantNumberParameterProcedure.execute(index, "restaurants",
																OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "level"));
														ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
																OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready");
													}
													ModifyRestaurantNumberParameterProcedure.execute(Math.floor(world.dayTime() / 24000d), index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
															OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "last_day_open");
													ModifyRestaurantLogicParameterProcedure.execute(true, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
															OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open");
													ModifyRestaurantNumberParameterProcedure.execute(world.dayTime() % 24000 + 8000, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
															OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "close_time");
													OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.add(owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
													OmnichefModVariables.MapVariables.get(world).markSyncDirty();
													RestaurantIsOpenProcedure.execute(world, entity, ChairsMax);
													CreateGUIDataTransferProcedure.execute(world, entity);
													if (owner instanceof Player _player27 && !_player27.level().isClientSide())
														_player27.displayClientMessage(Component.literal("Restaurant opened!").withStyle(ChatFormatting.GREEN), true);
													if (owner instanceof Player _player30 && !_player30.level().isClientSide())
														_player30.displayClientMessage(Component
																.literal(("Active setup: " + new java.text.DecimalFormat("#").format(AmountOfTables) + " Service Tables, " + new java.text.DecimalFormat("#").format(AmountOfChairs) + " Chairs."))
																.withStyle(ChatFormatting.WHITE), false);
												} else {
													ModifyRestaurantStringParameterProcedure.execute(index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
															OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception", "");
													if (owner instanceof Player _player33 && !_player33.level().isClientSide())
														_player33.displayClientMessage(Component.literal("A valid Reception is required to open your restaurant.").withStyle(ChatFormatting.RED), true);
												}
											} else {
												if (owner instanceof Player _player36 && !_player36.level().isClientSide())
													_player36.displayClientMessage(Component.literal("At least one valid Chair is required.").withStyle(ChatFormatting.RED), true);
											}
										} else {
											if (owner instanceof Player _player39 && !_player39.level().isClientSide())
												_player39.displayClientMessage(Component.literal("At least one valid Service Table is required.").withStyle(ChatFormatting.RED), true);
										}
									} else {
										if (owner instanceof Player _player42 && !_player42.level().isClientSide())
											_player42.displayClientMessage(Component.literal("Opening hours: 6:00\u201315:00.").withStyle(ChatFormatting.RED), true);
									}
								} else {
									if (owner instanceof Player _player45 && !_player45.level().isClientSide())
										_player45.displayClientMessage(Component.literal("Your restaurant has already opened today.").withStyle(ChatFormatting.RED), true);
								}
							} else {
								if (owner instanceof Player _player48 && !_player48.level().isClientSide())
									_player48.displayClientMessage(Component.literal("Restaurant data is not ready. Try again.").withStyle(ChatFormatting.RED), true);
							}
						}
					} else {
						if (owner instanceof Player _player51 && !_player51.level().isClientSide())
							_player51.displayClientMessage(Component.literal("Restaurants can only open in the Overworld.").withStyle(ChatFormatting.RED), true);
					}
				} else {
					if (owner instanceof Player _player54 && !_player54.level().isClientSide())
						_player54.displayClientMessage(Component.literal("Select your restaurant area before opening.").withStyle(ChatFormatting.RED), true);
				}
			}
		}
	}
}